FROM eclipse-temurin:21-jre-noble as BuildJava

RUN apt-get update && \
    apt-get install -y tesseract-ocr wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /usr/share/tesseract-ocr/5/tessdata/
RUN wget https://github.com/tesseract-ocr/tessdata/raw/main/deu.traineddata -P /usr/share/tesseract-ocr/5/tessdata/
RUN wget https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata -P /usr/share/tesseract-ocr/5/tessdata/

ARG JAR_FILE=build/libs/spring-ocr-tesseract-0.0.1.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]