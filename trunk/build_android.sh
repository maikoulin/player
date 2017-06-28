#! /usr/bin/env bash

BUILD_TARGET=$1

case "$BUILD_TARGET" in
    clean)
				#clean ijk
				cd android
				./compile-ijk.sh $1
				cd -

				#clean ffmpeg
				cd android/contrib
				./compile-ffmpeg.sh $1
				cd -

				#clean openssl
				cd android/contrib
				./compile-openssl.sh $1
				cd -
    ;;
    cleancode)
				#clean ijk
				cd android
				./compile-ijk.sh $1
				cd -

				#clean ffmpeg
				cd android/contrib
				./compile-ffmpeg.sh $1
				cd -

				#clean openssl
				cd android/contrib
				./compile-openssl.sh $1
				cd -
				
				#delete codes
				cd ijkmedia
				rm -rf ijkyuv
				cd -
				cd android/contrib
				rm -rf build
				rm -rf ffmpeg-*
				rm -rf openssl-*
				cd -
    ;;    
    ""|armv5|armv7a|arm64|x86|x86_64|all|all32|all64)
				#copy code first
				./copy_code.sh

				#compile openssl
				cd android/contrib
				./compile-openssl.sh $1
				cd -

				#compile ffmpeg
				cd android/contrib
				./compile-ffmpeg.sh $1
				cd -

				#compile ijk
				cd android
				./compile-ijk.sh $1
				cd -
    ;;
    *)
        echo "Usage:"
        echo "  build_android.sh armv5|armv7a|arm64|x86|x86_64"
        echo "  build_android.sh all|all32"
        echo "  build_android.sh all64"
        echo "  build_android.sh clean"
        echo "  build_android.sh cleancode"
    ;;    
esac

