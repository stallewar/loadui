#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  loadUI Runner Bootstrap Script                                          ##
##                                                                          ##
### ====================================================================== ###

### $Id$ ###

DIRNAME=`dirname $0`

# OS specific support (must be 'true' or 'false').
cygwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
esac

# Setup LOADUI_HOME
if [ "x$LOADUI_RUNNER_HOME" = "x" ]
then
    # get the full path (without any relative bits)
    LOADUI_RUNNER_HOME=`cd $DIRNAME/; pwd`
fi
export LOADUI_RUNNER_HOME

LOADUI_RUNNER_CLASSPATH="$LOADUI_RUNNER_HOME:$LOADUI_RUNNER_HOME/lib/*"

# For Cygwin, switch paths to Windows format before running java
if $cygwin
then
    LOADUI_RUNNER_HOME=`cygpath --path -w "$LOADUI_RUNNER_HOME"`
    LOADUI_RUNNER_CLASSPATH=`cygpath --path -w "$LOADUI_RUNNER_CLASSPATH"`
fi 

JAVA_OPTS="-Xms128m -Xmx1024m -XX:MaxPermSize=256m"

java $JAVA_OPTS -cp "$LOADUI_RUNNER_CLASSPATH" org.apache.felix.main.Main "$@"
