# NextOnlineTime

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/352b53ae062b44e580e6e9fb0646a8cf)](https://app.codacy.com/gh/NextPlugins/NextOnlineTime?utm_source=github.com&utm_medium=referral&utm_content=NextPlugins/NextOnlineTime&utm_campaign=Badge_Grade)

Um simples sistema de registro de tempo online de cada jogador no servidor, podendo criar recompensas por um tempo mínimo, com menus, top jogadores online,
filtro de recompensas, e com uma [API](https://github.com/NextPlugins/NextOnlineTime/blob/dev/src/main/java/com/nextplugins/onlinetime/api/NextOnlineTimeAPI.java) para desenvolvedores.
Veja o vídeo mostrando todo o sistema [aqui](https://www.youtube.com/watch?v=jMhaaaxmgME) 

Suporta conversão de outros plugins parecidos como AtlasTempoOnline e OnlineTimePlus, [vídeo demonstrativo](https://www.youtube.com/watch?v=8naKKD7pa8E).

## Comandos
|Comando               |Descrição           |Permissão             |
|----------------------|--------------------|----------------------|
|/tempo                |Exibe todos os sub-comandos do plugin|`Nenhuma`|
|/tempo ver            |Exibe o tempo online de um jogador, caso não insira nenhum, mostrará o seu|`Nenhuma`|
|/tempo menu           |Menu do sistema, mostrando as recompensas, top jogadores, opção de filtro e seu tempo online|`Nenhuma`
|/tempo enviar         |Enviar tempo online a um jogador|`nextonlinetime.sendtime`|
|/conversion           |Converte os dados do plugin selecionado|`nextonlinetime.admin`|

## Download

Você pode encontrar o plugin pronto para baixar [**aqui**](https://github.com/NextPlugins/NextOnlineTime/releases), ou se você quiser, pode optar por clonar o repositório e dar
build no plugin com suas alterações.

## Configuração
O plugin conta com quatro arquivos de configuração `config.yml`, `conversors.yml`, `messages.yml`, `npc.yml` e `rewards.yml`, em que você pode configurar o sql, recompensas, modo de funcionamento,
mensagens, conversores e outras opções.

## Dependências
O plugin não precisa de nenhuma dependência. As dependências de desenvolvimento são automaticamente baixadas por causa da tecnologia `PDM`

Caso queira usar o sistema de **NPC** os plugins `Citizens` e `HolographicDisplays` serão necessários

### Tecnologias usadas
-   [Google Guice](https://github.com/google/guice) - Fornece suporte para injeção de dependência usando anotações.
-   [PDM](https://github.com/knightzmc/pdm) - Baixa as dependências de desenvolvimento assim que o plugin é ligado pela primeira vez.

**APIs e Frameworks**

-   [command-framework](https://github.com/SaiintBrisson/command-framework) - Framework para criação e gerenciamento de comandos.
-   [inventory-api](https://github.com/HenryFabio/inventory-api) - API para criação e o gerenciamento de inventários customizados.
-   [sql-provider](https://github.com/henryfabio/sql-provider) - Provê a conexão com o banco de dados.
