#! /usr/bin/env bash

#copy ffmpeg
function ffmpeg_pull_fork()
{
    echo "== pull ffmpeg fork $1 =="
		if [ ! -d android/contrib/ffmpeg-$1 ]; then
		    echo "== copy ffmpeg $1 =="
		    cp -r extra/ffmpeg31 android/contrib/ffmpeg-$1
	  else
	      echo "== already copy ffmpeg $1 =="
    fi
}

ffmpeg_pull_fork "armv5"
ffmpeg_pull_fork "armv7a"
ffmpeg_pull_fork "arm64"
ffmpeg_pull_fork "x86"
ffmpeg_pull_fork "x86_64"

#copy libyuv
echo "== pull libyuv fork =="
if [ ! -d ijkmedia/ijkyuv ]; then
    echo "== copy libyuv =="
    cp -r extra/libyuv ijkmedia/ijkyuv
else
    echo "== already copy libyuv =="
fi

#copy openssl
function openssl_pull_fork()
{
    echo "== pull openssl fork $1 =="
		if [ ! -d android/contrib/openssl-$1 ]; then
		    echo "== copy openssl $1 =="
		    cp -r extra/openssl android/contrib/openssl-$1
    else
    		echo "== already pull openssl fork $1 =="
    fi
}

openssl_pull_fork "armv5"
openssl_pull_fork "armv7a"
openssl_pull_fork "arm64"
openssl_pull_fork "x86"
openssl_pull_fork "x86_64"
