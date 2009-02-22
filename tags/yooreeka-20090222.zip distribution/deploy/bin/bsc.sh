export IWEB2_HOME=/home/babis/myWork/iWeb2

export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/blas.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/commons-codec-1.3.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/commons-httpclient-3.1.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/commons-lang-2.3.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/commons-logging-1.1.1.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/f2jutil.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/jfreechart.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/jigg-0.1.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/junit-4.1.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/lapack.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/log4j.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/lucene-analyzers-2.3.0.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/lucene-core-2.3.0.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/lucene-demos-2.3.0.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/lucene-memory-2.3.0.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/nekohtml.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/poi-3.0.2-FINAL-20080204.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/resolver.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/secondstring-20070327.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/serializer.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/tm-extractors-1.0.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/xercesImpl.jar
export LIBJARS=$LIBJARS:$IWEB2_HOME/lib/xml-apis.jar

export CLASSPATH=$IWEB2_HOME/deploy/lib/bsh-2.0b4.jar:$LIBJARS
export CLASSPATH=$CLASSPATH:$IWEB2_HOME/deploy/lib/iweb2.jar
export CLASSPATH=$CLASSPATH:$IWEB2_HOME/deploy/conf/

java -Xms256M -Xmx256M -cp $CLASSPATH bsh.Interpreter


