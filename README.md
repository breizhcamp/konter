# Konter

Administration des conférences, des speakers et du programme

## Installation du poste en local

### pre-commit

On utilise le framework pre-commit afin d'analyser et de corriger la syntaxe des fichiers.

L'exécution se fait au moment du commit git, ou en exécutant la commande ```pre-commit run --all-files```.

Pour installer pre-commit, plusieurs méthodes :

* En utilisant pip : ```pip install pre-commit```
* En utilisant mise : ```mise use -g pre-commit```

On installera ensuite les hooks à partir du fichier de configuration **.pre-commit-config.yaml** :

```pre-commit install```

Hooks mise en place :

* **trailing-whitespace** : supprimer les espaces en fin de ligne
* **end-of-file-fixer** : pour que les fichiers se terminent par une nouvelle ligne
* **check-yaml** : vérifier la syntaxe des fichiers yaml
* **check-json** : vérifier la syntaxe des fichiers json
* **check-added-large-files** : pour ne pas commit les gros fichiers, comme les binaires
* **check-merge-conflict** : vérifier qu'il n'y a pas de conflits de fusion git
* **gitleaks** : pour identifier les secrets dans les fichiers à commit

### run.sh

On utilisera l'utilitaire **run.sh** pour gérer les containers Docker, générer une image Docker du projet **konter**
et analyser le code.

Usage et commandes :

```
Usage run.sh [OPTIONS] COMMAND
Commands:
start               Initialize and creating containers
stop                Stopping containers
down                Stopping/removing containers, networks and volumes
build               Building docker image
lint                Lint the Dockerfile
gitleaks            Detecting secrets like passwords, API keys, and tokens in files
Options:
-d, --dev           Starting konter container with a dev profil
-p, --prod          Starting konter container with a prod profil
-v, --verbose       Make the command more talkative
-h, --help          Display help
```

### Builder l'image Docker

Pour construire l'image Docker de l'application en local, on utilisera la commande ```./run.sh build```.

Un scan trivy est effectuée afin de rechercher si cette image contient des failles de sécurité.

### Gérer les containers

Pour démarrer, stopper ou supprimer les containers, on utilisera les commandes suivantes :

* ```./run.sh start```
* ```./run.sh stop```
* ```./run.sh down```

L'option **--dev** ou **--prod** permet également de démarrer le container konter, dont l'image est construite
à partir des sources en local.

Exemple :

```
./run.sh start --dev
Creating and starting Docker containers
...
[+] Running 4/4
 ✔ konter               Built                                                                                                                                                                                                                            0.0s
 ✔ Network konter       Created                                                                                                                                                                                                                          0.0s
 ✔ Container konter     Started                                                                                                                                                                                                                          0.2s
 ✔ Container konter-db  Starte
```

Remarque :

* Avec l'option **--dev**, il est nécessaire de démarrer les containers du projet **kalon** 
* On pourra vérifier que le container avec bien démarré avec la commande ```docker logs konter```.

### Analyser la syntaxe du code

Pour analyser le fichier Dockerfile, on utilisera la commande ```./run.sh lint```.

### Identifier les secrets

Pour vérifier que l'on ne commit pas des mots de passe, clés d'API ou token dans le dépôt git, on utilisera
gitleaks pour analyser les fichiers.

Exemple :

```
./run.sh gitleaks
Execute a scan with gitleaks and pre-commit to find secrets
Detect hardcoded secrets.................................................Passed
- hook id: gitleaks
- duration: 0.37s

INF 0 commits scanned.
INF scanned ~0 bytes (0) in 134ms
INF no leaks found
```
