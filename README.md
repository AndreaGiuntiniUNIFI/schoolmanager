# SchoolManager
Advanced Programming Techniques Project

[![Build Status](https://travis-ci.org/LeonardoScommegna/schoolmanager.svg?branch=master)](https://travis-ci.org/LeonardoScommegna/schoolmanager)
[![Coverage Status](https://coveralls.io/repos/github/LeonardoScommegna/schoolmanager/badge.svg?branch=master)](https://coveralls.io/github/LeonardoScommegna/schoolmanager?branch=master)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=LeonardoScommegna_schoolmanager&metric=alert_status)](https://sonarcloud.io/dashboard?id=LeonardoScommegna_schoolmanager)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=LeonardoScommegna_schoolmanager&metric=bugs)](https://sonarcloud.io/dashboard?id=LeonardoScommegna_schoolmanager)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=LeonardoScommegna_schoolmanager&metric=code_smells)](https://sonarcloud.io/dashboard?id=LeonardoScommegna_schoolmanager)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=LeonardoScommegna_schoolmanager&metric=sqale_index)](https://sonarcloud.io/dashboard?id=LeonardoScommegna_schoolmanager)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=LeonardoScommegna_schoolmanager&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=LeonardoScommegna_schoolmanager)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=LeonardoScommegna_schoolmanager&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=LeonardoScommegna_schoolmanager)


## Prerequisiti
* Maven (>=3.3.9)
* Docker
* Connessione Internet per la prima esecuzione

### Server VNC 
Per evitare un bug legato ad AssertJ Swing durante la fase test si consiglia l'utilizzo di un server VNC.

Il file `execute-on-vnc.sh` crea un desktop secondario tramite TightVNC, esegue lo script passatogli come argomento (in questo caso la build di Maven) nel desktop secondario, ed infine elimina il desktop all'uscita della build. È quindi richiesta l'installazione del pacchetto TightVNC:
```
$ sudo apt-get install -y tightvncserver
```

## Avvio dei Test
Il comando completo per l'esecuzione della build e dei test con Maven è il seguente, da eseguire nella root directory della repository:
```bash
$ ./execute-on-vnc.sh mvn -f schoolmanager-aggregator/pom.xml clean verify
```
Se il file `$HOME/.vnc/passwd` non è presente (non sono mai stati creati display secondari), è richiesto l'inserimento di una password di minimo 6 caratteri (necessaria) e di una password di visualizzazione (non necessaria, inserire \texttt{n} quando richiesta).

### Test con JaCoCo e PIT  
Sono definiti dei profili per il calcolo della code coverage con JaCoCo e per l'esecuzione della mutation analysis tramite PIT: per attivarli è sufficiente aggiungere le seguenti opzioni al comando precedente:
```bash
-P enable-jacoco -P enable-mutation-testing
```
## Avvio dell'applicazione

* Avviare il container docker attraverso il seguente comando digitato dalla root della repository
```bash
mvn -f schoolmanager-parent/pom.xml docker:start
```
* Eseguire il seguente comando dalla root della repository:
```bash
java -jar schoolmanager-frontend/target/schoolmanager-frontend-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```
L'applicazione utilizza il database PostgreSQL di default se si desidera cambiarlo è possibile farlo attraverso l'opzione `-p` o `--persistence-unit-name`, scegliendo tra `MYSQL` e `POSTGRES`
```bash
java -jar schoolmanager-frontend/target/schoolmanager-frontend-0.0.1-SNAPSHOT-jar-with-dependencies.jar -p MYSQL
```
