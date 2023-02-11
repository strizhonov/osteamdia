### Osteamdia app

##### (some play on words, created from Steam + Ost-India Trading company)<br/>

The application is intended to get and enrich CS GO skins trading data from your account and save it to local repository
for further analysis.

*** 

#### Prerequisites for running locally:

- maven
- docker 18.06.0+

> #### Steps to run:
>
> 1. Login to [steam](https://steamcommunity.com/market/) via browser and find a cookie with name `steamLoginSecure`.
     Copy its value.
> 2. Think up a database password
> 3. Run `POSTGRES_PASSWORD="your-db-password" STEAM_TOKEN="your-cookie-value" docker compose up -d` from bash
>
> It will build the application and run it in docker. The logs or the database may be viewed via docker communication
(parameters of the containers described in `docker-compose.yaml`)