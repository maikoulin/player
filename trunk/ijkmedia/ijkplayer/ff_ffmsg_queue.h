/*
 * ff_ffmsg_queue.h
 *      based on PacketQueue in ffplay.c
 *
 * Copyright (c) 2013 Zhang Rui <bbcallen@gmail.com>
 *
 * This file is part of ijkPlayer.
 *
 * ijkPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * ijkPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ijkPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#ifndef FFPLAY__FF_FFMSG_QUEUE_H
#define FFPLAY__FF_FFMSG_QUEUE_H

#include "ff_ffinc.h"
#include "ff_ffmsg.h"

// #define FFP_SHOW_MSG_RECYCLE

typedef struct AVMessage {
    int what;
    int arg1;
    int arg2;
	//added by wuym for Seeking issue
    int64_t timeout;
    struct AVMessage *next;
} AVMessage;

typedef struct MessageQueue {
    AVMessage *first_msg, *last_msg;
    int nb_messages;
    int abort_request;
    SDL_mutex *mutex;
    SDL_cond *cond;

    AVMessage *recycle_msg;
    int recycle_count;
    int alloc_count;
} MessageQueue;

inline static int msg_queue_put_private(MessageQueue *q, AVMessage *msg)
{
    AVMessage *msg1;
    AVMessage *prev, *next;

    if (q->abort_request)
        return -1;

#ifdef FFP_MERGE
    msg1 = av_malloc(sizeof(AVMessage));
#else
    msg1 = q->recycle_msg;
    if (msg1) {
        q->recycle_msg = msg1->next;
        q->recycle_count++;
    } else {
        q->alloc_count++;
        msg1 = av_malloc(sizeof(AVMessage));
    }
#ifdef FFP_SHOW_MSG_RECYCLE
    int total_count = q->recycle_count + q->alloc_count;
    if (!(total_count % 10)) {
        av_log(NULL, AV_LOG_DEBUG, "msg-recycle \t%d + \t%d = \t%d\n", q->recycle_count, q->alloc_count, total_count);
    }
#endif
#endif
    if (!msg1)
        return -1;

    *msg1 = *msg;
    msg1->next = NULL;
	//modified by wuym for Seeking issue
    if (!q->last_msg) {
        q->first_msg = msg1;
        q->last_msg = msg1;
    } else {
        prev = next = q->first_msg;
        for (; next != NULL; prev = next, next = next->next) {
            if (msg1->timeout < next->timeout) {
                if (q->first_msg == next) {
                    msg1->next = next;
                    q->first_msg = msg1;
                } else {
                    prev->next = msg1;
                    msg1->next = next;
                }
                break;
             }
        }
        if (next == NULL) {
            prev->next = msg1;
            q->last_msg = msg1;
        }
    }
    q->nb_messages++;
    SDL_CondSignal(q->cond);
    return 0;
}

inline static int msg_queue_put(MessageQueue *q, AVMessage *msg)
{
    int ret;

    SDL_LockMutex(q->mutex);
    ret = msg_queue_put_private(q, msg);
    SDL_UnlockMutex(q->mutex);

    return ret;
}

inline static void msg_init_msg(AVMessage *msg)
{
    memset(msg, 0, sizeof(AVMessage));
}

inline static void msg_queue_put_simple1(MessageQueue *q, int what)
{
    AVMessage msg;
    msg_init_msg(&msg);
    msg.what = what;
    msg_queue_put(q, &msg);
}

inline static void msg_queue_put_simple2(MessageQueue *q, int what, int arg1)
{
    AVMessage msg;
    msg_init_msg(&msg);
    msg.what = what;
    msg.arg1 = arg1;
    msg_queue_put(q, &msg);
}

inline static void msg_queue_put_simple3(MessageQueue *q, int what, int arg1, int arg2)
{
    AVMessage msg;
    msg_init_msg(&msg);
    msg.what = what;
    msg.arg1 = arg1;
    msg.arg2 = arg2;
    msg_queue_put(q, &msg);
}

inline static void msg_queue_put_simple4(MessageQueue *q, int what, int arg1, int arg2, int64_t timeout)
{
    AVMessage msg;
    msg_init_msg(&msg);
    msg.what = what;
    msg.arg1 = arg1;
    msg.arg2 = arg2;
    msg.timeout = timeout;
    msg_queue_put(q, &msg);
}

inline static void msg_queue_init(MessageQueue *q)
{
    memset(q, 0, sizeof(MessageQueue));
    q->mutex = SDL_CreateMutex();
    q->cond = SDL_CreateCond();
    q->abort_request = 1;
}

inline static void msg_queue_flush_timeout(MessageQueue *q, int64_t elapsed)
{
    AVMessage *msg;
    for (msg = q->first_msg; msg != NULL; msg = msg->next) {
        if (elapsed == -1) {
            msg->timeout = 0;
        } else {
            msg->timeout -= elapsed;
        }
    }
}

inline static void msg_queue_flush(MessageQueue *q)
{
    AVMessage *msg, *msg1;

    SDL_LockMutex(q->mutex);
    for (msg = q->first_msg; msg != NULL; msg = msg1) {
        msg1 = msg->next;
#ifdef FFP_MERGE
        av_freep(&msg);
#else
        msg->next = q->recycle_msg;
        q->recycle_msg = msg;
#endif
    }
    q->last_msg = NULL;
    q->first_msg = NULL;
    q->nb_messages = 0;
    SDL_UnlockMutex(q->mutex);
}

inline static void msg_queue_destroy(MessageQueue *q)
{
    msg_queue_flush(q);

    SDL_LockMutex(q->mutex);
    while(q->recycle_msg) {
        AVMessage *msg = q->recycle_msg;
        if (msg)
            q->recycle_msg = msg->next;
        av_freep(&msg);
    }
    SDL_UnlockMutex(q->mutex);

    SDL_DestroyMutex(q->mutex);
    SDL_DestroyCond(q->cond);
}

inline static void msg_queue_abort(MessageQueue *q)
{
    SDL_LockMutex(q->mutex);

    q->abort_request = 1;

    SDL_CondSignal(q->cond);

    SDL_UnlockMutex(q->mutex);
}

inline static void msg_queue_start(MessageQueue *q)
{
    SDL_LockMutex(q->mutex);
    q->abort_request = 0;

    AVMessage msg;
    msg_init_msg(&msg);
    msg.what = FFP_MSG_FLUSH;
    msg_queue_put_private(q, &msg);
    SDL_UnlockMutex(q->mutex);
}

/* return < 0 if aborted, 0 if no msg and > 0 if msg.  */
inline static int msg_queue_get(MessageQueue *q, AVMessage *msg, int block)
{
    AVMessage *msg1;
    int ret;

    SDL_LockMutex(q->mutex);

    for (;;) {
		//modified by wuym for Seeking issue
        msg1 = q->first_msg;
        int64_t timeout = msg1 ? msg1->timeout : 0;
        int64_t  timeoutMs  = (timeout + 999) / 1000;    
        uint64_t wait_start = SDL_GetTickHR();
        int64_t  to_wait    = timeoutMs;
        if (q->abort_request) {
            ret = -1;
            break;
        }

        if ((msg1 && msg1->timeout <= 0)) {
            q->first_msg = msg1->next;
            if (!q->first_msg)
                q->last_msg = NULL;
            q->nb_messages--;
            *msg = *msg1;
#ifdef FFP_MERGE
            av_free(msg1);
#else
            msg1->next = q->recycle_msg;
            q->recycle_msg = msg1;
#endif
            ret = 1;
            break;
        } else if (!block) {
            ret = 0;
            msg_queue_flush_timeout(q, -1);
            break;
        } else {
            if (!msg1)
                SDL_CondWait(q->cond, q->mutex);
            else {
                SDL_CondWaitTimeout(q->cond, q->mutex, to_wait);
                if (to_wait >= 0) {
                    uint64_t now = SDL_GetTickHR();
                    if (now < wait_start) {
                    // tick overflow
                        msg_queue_flush_timeout(q, -1);
                    } else {
                        uint64_t elapsed = now - wait_start;
                        msg_queue_flush_timeout(q, elapsed*1000);
                    }
               }
			}
        }
    }
    SDL_UnlockMutex(q->mutex);
    return ret;
}

inline static void msg_queue_remove(MessageQueue *q, int what)
{
    AVMessage **p_msg, *msg, *last_msg;
    SDL_LockMutex(q->mutex);

    last_msg = q->first_msg;

    if (!q->abort_request && q->first_msg) {
        p_msg = &q->first_msg;
        while (*p_msg) {
            msg = *p_msg;

            if (msg->what == what) {
                *p_msg = msg->next;
#ifdef FFP_MERGE
                av_free(msg);
#else
                msg->next = q->recycle_msg;
                q->recycle_msg = msg;
#endif
                q->nb_messages--;
            } else {
                last_msg = msg;
                p_msg = &msg->next;
            }
        }

        if (q->first_msg) {
            q->last_msg = last_msg;
        } else {
            q->last_msg = NULL;
        }
    }

    SDL_UnlockMutex(q->mutex);
}

#endif
