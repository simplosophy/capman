#!/bin/sh
protoc io/capman/internal/*.proto --java_out=../java/capman-common/src/main/java/
