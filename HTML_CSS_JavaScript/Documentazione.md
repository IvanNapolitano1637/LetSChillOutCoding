# Documentazione pagine HTML

## Introduzione

Ciao! Questa repo contiene una quarantina di pagine HTML divise in alcune categorie ed in questo file te ne parlo un po'. Le ho divise per cartelle e categorie perché cominciavano a diventare troppe ed era diventato dispersivo averle tutte insieme in un unico punto.

Queste pagine sono un gioco, un passatempo, sia per me che le ho scritte che per l'eventuale utente. Non vanno prese seriamente. Sono ancora in fase di modifica, in questo periodo ci sto lavorando più volte a settimana. Sono passati anche interi mesi senza che facessi nulla, sono stato più volte convinto di aver terminato il lavoro su alcune di esse per poi tornare a metterci mano. Chissà, forse su nessuna si può può avere la garazia che sia davvero finita finché non sarò morto.

Ci sto lavorando da tanto tempo, forse dal 2019/2020, non ricordo. Quando ho iniziato ad utilizzare l'intelligenza artificiale hanno avuto un rapido sviluppo e c'è stato anche un salto di qualità.

Funzionano sia da cellulare sia da pc. Dato che sostanzialmente nessuno ha mostrato un sincero interesse ho limitato lo studio della grafica ed i controlli di funzionamento al mio telefono Adroid ed al mio calcolatore con Windows. Dubito funzioni su altre famiglie di sistemi operativi ed ho visto che non funziona su diversi tipi di smartphone. Non ho nemmeno provato a vedere come vanno su terze tipologie di dispositivo come, ad esempio, tablet semplicemente perché al momento non ne ho. Un giorno potrei pensare di allargare la loro funzionalità a questi nuovi dispositivi ma questa operazione non è ancora in programma.

Nasco come programmatore di linguaggi compilati e fortemente tipizzati come C++ e Java. Il front-end lo vivo più come compromesso per far diffondere più semplicemente alcune mie creazioni anche a chi è a digiuno di informatica.

I codici sono scritti esclusivamente in inglese con un'unica eccezione di cui parlero quando tratterò di quella specifica pagina. Alcune pagine sono solo in italiano, in gran parte hanno il nome in inglese, alcune sono in dodici lingue (Inglese, Italiano, Francese, Spagnolo, Tedesco, Portoghese, Romeno, Ucraino, Greco, Latino, Cinese, Giapponese), poche prive di lingue. Qualcuna è solo italiana perché o non trovo abbia senso in altre lingue oppure perché ancora non l'ho fatta tradurre nelle altre undici ma un giorno conto di farlo. Di ogni pagina è segnalato il numero di lingue.

Ogni pagina ha quattro identificativi:
* Un suo **nome** che appare anche nel file index.html ed è l'unico che davvero deve contare per te.
* Un **codice numerico** che è semplicemente un numero progressivo per ricordarmi quale ho fatto prima e quale dopo, qual è stata la prima pagina, quale l'ultima e cose così. Non è troppo sensato. Si basa solo sulla "nascita" della pagina ma molte sono state cambiate/stravolte nel tempo. Non tutte contemporaneamente, non tutte allo stesso modo.
* Un **codice alfanumerico** quasi identico al precedente per tipologia e funzione con l'unica differenza che oltre al numero è associata anche una lettera che indica la categoria cui appartiene la pagina ed ogni lettera ha numeri che partono da uno.
* Un **nome** come se fosse un essere **umano**. Questo mi serve per chiamare le pagine in modo rapido e veloce, usando una sola parola. Una pagina contiene cinque sottopagine ed anche ciascuna di esse nha il proprio nome umano. Qualche scelta ha una logica, un criterio, altre no. Non farti troppe domande su perché io abbia chiamato la pagina prorpio così, magari come te. Potresti non poterci mai arrivare. Potrei non saperti dare nemmeno io una spiegazione valida. O la spiegazione potrebbe non piacerti.

Questi quattro identificativi si trovano come commento nel secondo rigo di ciascuna pagina ad esclusione di quella complessiva. In index.html al secondo rigo come commento c'è il link del medesimo file sulla repo.

Per passare da queste pagine al file index.html che le contiene tutte utilizzo una classe Java che ho inserito nella repo ma fuori da questa sezione. Nel file java ho spiegato come fare. Se stai leggendo queste righe è facile tu mi conosca e fai prima a chiedermelo di persona o su WhatsApp XD.

Ci sono anche dei giochi ed in nessuna pagina è presente alcuna pubblicità e quasi certamente ce ne sarà mai alcuna. Funzionano tutte anche senza connessione internet. Ogni file è autonomo e sufficiente a se stesso. Con alcuni giochi si può giocare anche contro la macchina ma ancora mai con altri utenti umani. Non so se aggiungerò mai questa possibilità. Al momento non mi attrae.

Alcune hanno anche comandi attivabili da tastiera. Alcune hanno anche un pop-up con la lista di comandi attivabili da tastiera. Il pop-up compare premendo il "?" e scompare o premendo sempre il "?" oppure cliccando sulla "x" in alto a destra come qualsiasi finestra di Windows.

## Veniamo alle pagine

In questo [link](https://github.com/IvanNapolitano1637/LetSChillOutCoding/tree/main/HTML_CSS_JavaScript) puoi trovare cartelle con alcune sottocartelle ed un file **index.html** esterno ad esse. Questo file contiene al suo interno tutte le altre pagine catalogate in determinate sezioni con gli stessi nomi delle cartelle e sottocartelle della repo.

Vediamo la struttura:

```text
.
└── HTML_CSS_JavaScript
    ├── Games
    │   ├──Board
    │   │  ├──Eight Queens.html
    │   │  ├──Connect Four.html
    │   │  ├──Chess.html
    │   │  ├──Checkers.html
    │   │  ├──Backgammon.html
    │   │  ├──Go.html
    │   │  └──Gioco dell'oca.html
    │   ├──Card
    │   │  ├──Solitario carte napoletane.html
    │   │  ├──Klondike.html
    │   │  ├──Spider.html
    │   │  ├──FreeCell.html
    │   │  ├──Poker.html
    │   │  ├──Burraco.html
    │   │  └──Scala 40.html
    │   ├──Math
    │   │  ├──Game of Life.html
    │   │  ├──Tower of Hanoi.html
    │   │  └──Modular Matching.html
    │   └──Puzzle
    │      ├──Fifteen.html
    │      ├──Puzzle.html
    │      └──Rubik's Cube.html
    ├── Tools
    │   ├──Clocks
    │   │  ├──Binary Clock.html
    │   │  ├──24h Clock.html
    │   │  ├──Orbit Clock.html
    │   │  ├──Strange Clock.html
    │   │  ├──World Time Zones.html
    │   │  ├──Digital Clock.html
    │   │  ├──Analog Clock.html
    │   │  └──Seven-Segment Digital Clock.html
    │   ├──Lab
    │   │  ├──Alpha-Numeric Converter.html
    │   │  ├──Sintetizzatore Fonetico Vocali Italiane.html
    │   │  ├──Animated Radial Menu.html
    │   │  ├──Sensore Impronta Digitale.html
    │   │  ├──Vibrations Explorer.html
    │   │  ├──Image Editor.html
    │   │  └──Centomila miliardi di poesie.html
    │   └──Utilities
    │      ├──Colors Pro.html
    │      ├──Some Maths.html
    │      ├──Sounds.html
    │      ├──Days.html
    │      ├──SuperEnalotto.html
    │      └──Colors Hub.html
    └── index.html
```

Veniamo ora alla spiegazione di ciascuna pagina. L'ordine con cui sono presenti è quello di creazione, quello dato dal secondo dei quattro codici di cui sopra.

### Binary Clock.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Orologio e calendario in numeri binari. Nella parte superiore ci sono anno, mese e giorno in quella inferiore ore, minuti e secondi. Il blue vale zero, il rosso vale uno. Ancora più giù ci sono i secondi trascorsi dalla mezzanotte del primo gennaio del 1970 (Timestamp) che puoi trovare anche [qui](https://www.epochconverter.com/). Ancora più giù c'è il mio numero di cellulare espresso in numeri binari. Ancora più giù nel primo campo il Timestamp in numeri decimali, nel secondo ancora lo stesso dato ma in esadecimale, nel terzo un conto alla rovescia al termine del quale per il Timestamp non saranno più sufficienti sessantaquattro bit o otto byte, nel quarto il mio numero di cellulare ma in esadecimale.

### Eight Queens.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Gioco delle [otto regine](https://it.wikipedia.org/wiki/Rompicapo_delle_otto_regine) con la possibilità di mutare la dimensione della scacchiera. Ha un semaforo che si può accendere o spegnere. Se il semaforo è acceso segnala al giocatore eventuali errori colorando il background di rosso. Ha due livelli di difficoltà. A livello facile segnala le caselle minacciate, a difficile no. Ha un pulsante che permette di decidere se mettere al massimo tante regine quanto l'ordine della scacchiera oppure una per singola casella. Limitatamente al caso con ordine otto ha un pulsante per poter vedere tutte le novantadue soluzioni divise nelle loro dodici famiglie. Due soluzioni appartengono alla stessa famiglia se l'una si può mutare nell'altra usando rotazioni e/o ribaltamenti della scacchiera. Ha un pulsante per ricominciare da zero lasciando immutate tutte le impostazioni.

### Game of Life.html ###

<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

[Gioco della vita](https://it.wikipedia.org/wiki/Gioco_della_vita). L'ho a lungo considerato un aborto. Aggiungendo il _Random_ è rinata ed ha trovato per me una sua dignità. L'unico difetto che noto e non credo di poter risolvere è che lo spazio è finito. Mi piacerebbe potesse essere infinito ma pazienza.

### 24h Clock.html ###

<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Orologio analogico. Sono partito copiando quello di [questo video](https://www.youtube.com/watch?v=Ki0XXrlKlHY&t=7s&ab_channel=WebDevSimplified) ma poi ho fatto numerose modifiche. Ha ventiquattro ore anziché dodici, di base va anche in senso antiorario ma se ne può invertire il senso a piacimento, si può scegliere tra numeri indo-arabi e numeri romani ed ha ben nove diverse regole per i colori:

1. Cambia colore ogni ora, sei colori in tutto. Ogni giorno esattamente quattro cicli.

2. Stessi sei colori ma cambia colore ogni secondo.

3. Cambia colore ogni secondo ma qui sono trenta.

4. Cambia colore ogni secondo ma qui sono dieci.

5. Stessi dieci colori ma ognuno dura sei secondi. Un ciclo intero al minuto.

6. Stessi dieci colori ma ognuno dura sei minuti. Un ciclo intero ogni ora.

7. Stessi dieci colori ma distribuiti durante l'intera giornata, ognuno per 2h 24m di fila.

8. Colore fisso ma casuale.

9. Quattro colori fissi, uno dalle 5:00 alle 8:00, uno dalle 8:00 alle 17:00, uno dalle 17:00 alle 20:00, uno dalle 20:00 alle 5:00 del giorno seguente.

### Colors Pro.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Pagina per "giocare" con dei colori. Mi serviva soprattutto per trovare il colore di sfondo da dare alle mie pagine. Modificando dei parametri si possono avere ben 4.294.967.296 diversi colori, ciascuno con il suo codice esadecimale. Il colore si ottiene come background della pagina. Ci sono degli slider per modificare le componenti sia **RGB** (rosso, verde e blue) sia **CMYK** (ciano, magenta, giallo e nero). C'è anche uno slider per ottenere il grigio corrispondente all'attuale colore ed un altro per schiarire o scurire il colore attuale. C'è un pulsante per ottenere direttamente il bianco, uno per il nero, uno per un colore preso completamente a caso, uno per il colore opposto a quello attualmente mostrato. Ci sono anche dei pulsanti per fare aumenti o riduzioni più o meno grandi sia di ciascuna componente RGB sia di tutte e tre in contemporanea. Dato che tutti questi strumenti hanno preso molto spazio ho inserito un "foro" per avere un campione del colore ed ho inserito la possibilità di variare il numero di elementi mostrati. Ci sono quattro possibili diverse visualizzazioni.

### Some Maths.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Amo la matematica e mi capita spesso di fare conti. Questa pagina è più per me che per altri. I numeri con cui lavora sono interi. Contiene un test di primalità (con sia Fermat che Miller Rabin), fa il quadrato e la radice quadrata (con resto), fa il triangolare e la radice triangolare (sempre con resto), fa la scomposizione in fattori primi, dati due numeri ne calcola il MCD e mostra la relazione di Bézout con cui ottenerlo, applica il criterio di primalità di Fermat calcola l'area di un poligono regolare, scrive l'input in tutti i modi possibili come somma di due quadrati e, per finire, mostra un poligono regolare con un numero di lati a piacere fino a sessanta sia con diagonali che senza. La funzione che fornisce la fattorizzazione sembre rendere superflua quella per la primalità. Non è così. A me capita di scomporre a mente numeri e mi capita di voler sapere se un numero è primo senza però sapere anche per chi è divisibile per non avere "spoiler" XD. Poi i criteri di primalità sono spesso molto più rapidi. Scrivendo questa spiegazione mi sono venute in mente un paio di cose da aggiungere alla pagina anche se non so se metterle qui o in nuove pagine dedicate.

### Tower of Hanoi.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

[Torre di Hanoi/Lucas](https://it.wikipedia.org/wiki/Torre_di_Hanoi) con possibilità di salvare/caricare la partita, ci sono tre slot disponibili. Sono presenti anche un contatore delle mosse ed un pulsante per far risolvere il livello in automatico sempre però partendo da zero. Ognuno può giocare in qualsiasi momento il livello che preferisce, non è necessario risolverne alcuni per passare ad altri. Non sono sicuro della velocità delle mosse dello svolgimento automatico perché il decimo livello richiede più di _**mille**_ mosse. Se facessi risolvere il decimo livello facendo eseguire una mossa al secondo ci vorrebbero ben diciassette minuti e tre secondi. Troppo.

### Orbit Clock.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Altro orologio analogico copiato da [un video di youtube](https://www.youtube.com/watch?v=FKQqFn2j0Ys&ab_channel=OnlineTutorials) e poi modificato a gusto mio. L'ho impostato sempre con ventiquattro ore, ho sempre dato la possibilità di invertire il senso di rotazione, ho inserito un due effetti sonori per lo scoccare dei secondi, uno per verso di rotazione.

### Sounds.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Quando ho iniziato a lavorare a questa pagina avevo in mente di fare l'equivalente acustico di quella dei colori. Avrei voluto farle podurre qualsiasi suono costante, ad esempio anche qualsiasi vocale prodotta da qualsiasi persona di mia conoscenza. Mi sono illuso. Non sembra fattibile. Almeno adesso ne so troppo poco. Comunque ora questa pagina produce suoni di frequenze da zero a ventiquattromila Hertz con quattro diversi oscillatori. L'orecchio umano dovrebbe poter ascoltare suoni solo fino a 20 kHz. Ha un pulsante per far partire il suono e per fermarlo. Uno per raddoppiare la frequenza ed uno per dimezzarla. Uno per ricaricare la pagina. Uno per attivare e disattivare la sirena scelta tra le sei disponibili. Poi ci sono i pulsanti per suonare le tredici note di un'ottava da DO a DO semitoni inclusi. Poi tre radio button uno per selezionare la frequenza massima (se ci serve una frequenza sensibilmente più bassa di 24 kHz può essere molto più comodo avere un massimo molto minore di quello) e due per scegliere l'oscillatore uno della frequenza semplice l'altro per la sirena. Per finire c'è un pulsante per switchare da questa versione base ad una professionale con altri otto slider ma non so bene cosa sono ed a che servono. Me li ha aggiunti un'IA.

### Days.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Fornisce il giorno della settimana di qualsiasi data dal primo gennaio dell'anno uno al trentuno dicembre tremilanovecentonovantanove. Permette anche di calcolare la differenza di giorni tra due date ciascuna delle quali nel medesimo range temporale. Ad esempio io tra due giorni, domenica ventiquattro maggio duemilaventisei, ne compio quindicimila.

### SuperEnalotto.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Fornisce sestine da giocare al SuperEnalotto. Sia prendendo in input un numero ordinale, sia prendendo in input un testo, sia completamente a caso. Quando la sestina parte da un testo scritto dall'utente ci sono tre cose da considerare. La prima è che l'input viene considerato come semplice sequenza di caratteri, non viene valutato l'aspetto semantico. Sostituire un termine con un sinonimo può portare ad una sestina completamente diversa. La seconda è che ad input uguale corrisponde una determinata sestina solo finché non arrivano le diciannove del giorno della settimana con la successiva estrazione, poi sarà completamente diversa. La terza è che anche nello stesso momento anche con lo stesso input si possono avere output diversi mutando dispositivo o impronta. Questa pagina è l'unica il cui codice è scritto in italiano seppur con qualche "schifezza" in factoryMethod e predicati. Credo che sia una pagina che non avrebbe senso tradurre in altre lingue se quei paesi non hanno questo tipo di lotteria con questi stessi numeri.

### Connect Four.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Forza Quattro. Ha quattro diverse difficoltà, si può decidere se far iniziare il pc o il giocatore umano e si può decidere se giocare con i gettoni gialli oppure con i rossi lasciando alla macchina l'altro colore. Le difficoltà lasciano un pochino a desiderare. Con le prime due tira quasi completamente a caso, con la quarta è un po' lento.

### Modular Matching.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Gioco matematico, un po' tecnico, roba di artimentica modulare, campi finiti. Ne ho fatto anche una versione in Java, anche meglio di questa qui. Si sceglie un numerio primo **p**, di base si parte con il trentasette, poi bisogna trovare tutte le coppie di inversi modulo p. La pagina mostra quante coppie sono state trovate ed il numero di errori fatti. Permette di evidenziare i doppi e gli opposti dei numeri già matchati così da trovare facilmente altre coppie. Se giochi da pc c'è anche la possibilità di barare azzerando il conteggio degli errori. Permette di scegliere anche un numero non primo ma lo sconglierei, ci sarebbero divisori dello zero che sono privi di inversi.

### Fifteen.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Gioco del quindici, molto semplice, ha giusto tre pulsanti: il primo per mescolare le caselle, il secondo per cambiare lingua ed il terzo per far risolvere il gioco alla macchina mostrando tutti i passaggi che fa.

### Strange Clock.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Ennesimo orologio. Fa un po' schifo ma è già migliorato molto rispetto ad altre versioni precedenti XD. Ha dodici cerchi colorati disposti a cerchio ed all'interno due numeri separati da un simbolo tipo π/Π. Il numero a sinistra indica il giorno, il suo colore il mese, il numero a destra il minuto, il suo colore l'ora. Per sapere a che numero corrisponde un dato colore basta vedere in che posizione si trova il cerchio di quel colore e valutare il numero in quella posizione nei classici orologi con le lancette.

### Chess.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Gioco degli scacchi. Puoi scegliere il colore, puoi scegliere un livello di difficoltà tra quattro, puoi scegliere se visualizzare o meno il registro delle mosse, puoi annullare le ultime mosse fino a tornare all'inizio della partita, puoi salvare la partita e ricaricare partite salvate. Hai tre slot di memoria.

### Checkers.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Gioco della Dama. Ci sono dodici tipi di dama, immagino siano tutti i tipi esistenti. Comincia sempre il bianco.

### Colors Hub.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Altra pagina per i colori. Continene cinque sottopagine. La prima è Colors_Pro di cui ho già parlato. Le successive tre, che un tempo erano pagine separate, sono versioni semplificate della prima perché non servivano effettivamente tutti quei colori. L'ultima è abbastanza recente è mi è piaciuta come idea, è abbastanza diversa dalle altre. Ognuna ha anche un pulsante per tornare all'indice delle cinque pagine. Entriamo nel dettaglio.

I. Ne abbiamo già parlato.

II. Ci sono solo sei possibilità per ogni componente R, G e B invece che 256. La pagina può mostrare duecentosedici colori invece che quattro miliardi e rotti. Poi ci sono dodici pulsanti. I primi otto sono per i casi in cui ognuno dei tre ha un valore o minimo o massimo ("000" nero, "00F" blue, "0F0" verde, "0FF" ciano, "F00" rosso, "F0F" magenta, "FF0" giallo, "FFF" bianco), poi ci sono due pulsanti per aumentare e diminuire tutte e tre componenti di una unità e ciò è consentito solo se tutti e tre hanno la possibilità di muoversi, poi c'è un pulsante per far selezionare un colore a caso e poi uno per tornare all'indice con le cinque pagine.

III. Questa versione ha esattamente gli stessi pulsanti della versione "II" ma qui per ogni componente R, G e B ci sono sedici possibilità per un totale di quattromilanovantasei colori rappresentabili.

IIII. Qui per ogni componente ci sono cinque possibili scelte per un totale di centoventicinque colori di sfondo. Poi ci sono sei pulsanti. Uno per rendere lo sfondo bianco, uno per renderlo nero, due per aumentare e diminuire tutte e tre le componenti di una unità e ciò anche qui è consentito solo se tutti e tre hanno la possibilità di muoversi, poi c'è un pulsante per far selezionare un colore a caso e poi uno per tornare all'indice con le cinque pagine.

IIIII. In questa sottopagina c'è un cerchio dove si può selezionare un colore con il puntatore, poi ci sono sette slider e più giù dieci pulsanti. Il primo slider fa ruotare il cursore di un angolo arbitrario lasciandolo sempre alla stessa distanza dal centro. Il secondo ne lascia l'angolo invariato e ne muta il raggio. Il terzo serve a variarne la luminosità e parte dal nero per arrivare al bianco. Quarto, quinto e sesto servono a variare rosso, verde e blue. Il settimo ha la stessa funzione del terzo con la differenza che al terzo è associato un valore espresso in percentuale mentre al settimo un valore numerico compreso tra zero e duecentocinquantacinque. Forse ne eliminerò uno dei due in futuro, vedremo. I primi otto pulsanti sono per gli stessi otto colori delle versioni II e III, poi c'è quello per il colore casuale, poi quello per tornare al menù.

### Backgammon.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Gioco del backgammon. Non so giocare a questo gioco quindi non la posso nemmeno testare per bene. Ho inserito anche qui la possibilità di annullare le mosse fino a tornare all'inizio della partita.

### Solitario carte napoletane.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Versione digitale di un gioco che faccio da molti anni con le carte napoletane, avrò iniziato da bambino. Non sono nemmeno sicuro le regole siano esatte. Qualcuna potrei averla modificata, qualcuna inventata. Potrei essere l'unico a giocare a "questa versione". Poi la rappresentazione delle carte fa oggettivamente schifo ma ancora non sono riuscito ad ottenere un risultato migliore di questo. C'è anche un pulsante per iniziare una nuova partita ed uno per annullare tutte le mosse fino a tornare all'inizio della partita.

### World Time Zones.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Pagina con tutti i fusi orari del mondo. Si può cercare uno specifico fuso orario inserendo una stringa, oppure vedere tutti quelli di uno specifico continente, oppure tutti quelli dove in questo momento è una certa ora oppure una combinazione di questi. Questa pagina ha due versioni, una chiara per il giorno ed una notturna scura.

### Digital Clock.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Semplice orologio digitale con ventiquattro ore da sessanta minuti da sessanta secondi.

### Analog Clock.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Semplice orologio analogico, con le ore il cui colore cambia costantemente. Cliccando sul quadrante si può passare da numeri indo-arabi a numeri romani e viceversa.

### Seven-Segment Digital Clock.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Calendario con data in formato GG/MM/AA ed orologio digitale con ventiquattro ore, minuti e secondi. Le cifre sono espresse con le classiche sette barre verdi che si vedono in vecchi orologi, sveglie e calcolatrici oppure in quasi tutte le bombe a tempo dei film americani XD.

### Alpha-Numeric Converter.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Paginetta nata per caso mentre cazzeggiavo in chat con un'amica. Puoi ignorarla. Serve a convertire stringhe in numeri e viceversa. Usa tre tipi di numeri (decimali, binari ed esadecimali) e tre divesi alfabeti. L'alfabeto da ventuno lettere è quello che imparai alle elementari dove alla "I" seguiva la "L" ed alla "V" la "Z". Quello da ventisei è lo stesso cui sono state aggiunte "J", "K", "W", "X" ed "Y". Quello da ventitrè è quello latino, cioè quello da ventisei tranne la "J" e la "W". Permette anche di fattorizzare i numeri decimali, di criptare e decriptare una stringa con la tecnica del cifrario di Cesare e di copiare il valore di uno dei quattro campi (la stringa alfabetica ed il numero nelle tre diverse basi). Anche questa pagina come quella dei fusi orari ha due versione estetiche, una più chiara ed una più scura.

### Sintetizzatore Fonetico Vocali Italiane.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Questa è nata per sopperire un pochino alla carenza della pagina dell'acustica. Questa pagina permette di riprodurre suoni simili a quelli delle sette vocali italiane e permette di fare variazioni su alcuni parametri. Non c'è molto da spiegare, basta vedere la pagina stessa.

### Go.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Il gioco del [go](https://it.wikipedia.org/wiki/Go_(gioco)) è simile a quello degli scacchi. Non ho ancora mai imparato a giocarvi e non ho potuto testare più di tanto questa pagina. Ci sono tre pulsanti per decidere la dimensione del goban 9x9, 13x13 e 19x19. Ce ne sono altri tre per passare la mossa, abbandonare la partita e iniziare una nuova partita.

### Animated Radial Menu.html ###
<span style="background-color: #7f7f7f; color: white; padding: 2px 6px; border-radius: 4px;color: #00FFFF;">**Nessuna lingua**</span>

Questa è attualmente inutile ma mi piaceva e mi sarebbe dispiaciuto perderla. Ho copiato il codice da questo [video](https://www.youtube.com/watch?v=koTXGYLkPBo) e probabilmente un giorno lo integrerò in qualche pagina più grossa.

### Sensore Impronta Digitale.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Come la precedente è inutile ma l'ho trovata un'idea carina. Finge di controllare l'impronta digitale. La risposta che dà è puramente casuale. Se utilizzata da cellulare emette anche piccole vibrazioni che rendono la pagina più credibile.

### Vibrations Explorer.html ###
<span style="background-color: #012169; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Inglese**</span>

Altra pagina inutile, fatta fare per gioco. Con questa si possono avvertire delle vibrazioni periodiche. Ci sono anche quattro loop preimpostati. Il primo simula il battico cardiaco, il secondo l'SOS, il terzo un battito accelerato ed il quarto è più lieve ma sempre rapido e costante. Ovviamente le vibrazioni possono essere avvertite solo da smartphone. Da pc si possono vedere solo le luci rosse nella sezione in partenza nera.

### Image Editor.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Permette di caricare un'immagine, modificarne i colori e salvare il risultato. I cambiamenti ai colori che consente riguardano le singole componenti R, G e B. Permette di aumentare o ridurre la componente di ciascuna di esse, di scambiare ciascuna di esse con qualunque altra, di farne il negativo e di portare l'immagine in scala di grigi. 

### Centomila miliardi di poesie.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Voleva essere una versione italiana dell'opera francese di Raymond Queneau del 1961 ma non è venuta molto bene. Ho fatto fare tutto a delle IA, non ci ho lavorato molto di persona. Potrei rimetterci mano un giorno. Le rime ci sono, funzionano, paiono corrette ma il senso spesso mi pare mancare. A me non comunicano molto quelli che mi sono capitati tra i centomila miliardi di possibili sonetti.

### Puzzle.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Puzzle. Ci sono otto immagini di base tra cui è possibile scegliere ma come nona opzione possiamo usare un'immagine nostra. Consiglio questo. Le prime otto trovo lascino un po' a desiderare. Sono presenti diverse dimensioni per ogni puzzle e quando l'immagine è caricata dall'utente i numeri di pezzi per lato sono correttamente adeguati. Anche qui è possibile salvare la partita. Si può anche scegliere se ogni pezzo debba comparire già correttamente orientato oppure se debba essere casualmente ruotato.

### Rubik's Cube.html ###
<span style="background-color: #FF0000; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFF00;">**Dodici lingue**</span>

Celeberrimo gioco. Questa sua versione digitale non mi convince molto. A pc vedo la pagina un po' soffocata. Da cellulare anche peggio. Sono presenti pulsanti per far fare precise rotazioni al cubo o a qualche faccia ma ancora non c'è un pulsante per automatizzare la soluzione come fatto per altri giochi. Piccolo "comportamento strano" (non so se chiamarlo "bug"): se all'inizio faccio una o più mosse e poi l'inverso nell'ordine inverso mi dice che ho risolto il cubo ma così è un po' barare XD.

### Klondike.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Solitario Klondike. Tre versioni. Tre carte libere, tre carte in ordine, carta singola. È possibile annullare qualsiasi sequenza di mosse ed anche cominciare una nuova partita.

### Gioco dell'oca.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Gioco dell'oca. Si possono scegliere da uno a quattro giocatori. Non ci ho ancora giocato, non è tra i miei preferiti. Non sono sicuro sia corretto o magari presenti cose da modificare.

### Spider.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Solitario Spider. Tre versioni: con un seme, con due semi, con quattro semi. È possibile annullare qualsiasi sequenza di mosse, avere suggerimenti ed anche cominciare una nuova partita.

### FreeCell.html ###
<span style="background-color: #012169; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Inglese**</span>

Solitario FreeCell. È possibile annullare qualsiasi sequenza di mosse e cominciare una nuova partita. Conta le mosse fatte.

### Poker.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Scritta e poi sostituita da un'altra fatta da zero. Non so giocare a poker e quindi non ho potuto notare una serie di disastri. Nemmeno ora sono sicuro sia perfetta. Per ora c'è solo la tipologia di poker più nota e diffusa. Nella precedente c'erano tutte ma ho dovuto cestinarla.

### Burraco.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Burraco. Non so giocarci e quindi non l'ho ancora testata per bene. Qualche modifica sicurametne andrà fatta, ad esempio nella frase iniziale. Dice che puoi giocare contro l'IA e che funziona perfettamente anche senza connessione ma queste cose sono ovvie e valgono per tutte, non vanno dette. Poi il suo chiamarla "Burraco Italiano" mi fa sospettare ce ne siano altre versioni. Se è falso va corretta la frase, se è vero vanno inserite pure quelle.

### Scala 40.html ###
<span style="background-color: #008c45; color: white; padding: 2px 6px; border-radius: 4px;color: #FFFFFF;">**Italiano**</span>

Scala 40. Non ci so giocare e non posso essere sicuro vada tutto bene. L'ho vista ancora poco.