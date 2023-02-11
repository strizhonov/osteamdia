FROM maven:3.9.0-amazoncorretto-17 as build
COPY ./ /usr/src/osteamdia-parent
WORKDIR /usr/src/osteamdia-parent
RUN mvn clean package -DskipTests

FROM openjdk:17
COPY --from=build /usr/src/osteamdia-parent/osteamdia/target/osteamdia.jar /usr/bin/osteamdia.jar
ARG STEAM_TOKEN
ARG POSTGRES_PASSWORD
ARG DB_HOST
ENV steamToken $STEAM_TOKEN
ENV postgresPassword $POSTGRES_PASSWORD
ENV dbHost $DB_HOST
CMD java "-DosteamdiaDbPassword=$postgresPassword" "-DdbHost=$dbHost" \
-jar /usr/bin/osteamdia.jar --app.secure.steam=$steamToken --app.sequentialHistory=true