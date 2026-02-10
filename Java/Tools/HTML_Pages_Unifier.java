import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

//Codice per creare un unico file HTML con all'interno tutte le pagine che stanno nelle tre cartelle "Clocks", "Games" e "Tools".
//Codice creato da Gemini e Claude lunedì nove febbraio duemilaventisei dopo un altro tentativo fatto con Gemini due giorni prima.
//Unica cosa su cui ragionare è l'estetica della nuova pagina creata. Ne ho viste di migliori. 
//Unica differenza tra le pagine originali e quelle contenute nel file creato qui è una sezione con un pulsante per tornare all'indice di tutte le pagine.
//Da mettere nella cartella in cui ci sono: "Clocks", "Games" e "Tools".
//Da compilare e lanciare lì da terminale.

public class HTML_Pages_Unifier {

    private static final String[] FOLDERS = {"Clocks", "Games", "Tools"};

    public static void main(String[] args) {
        StringBuilder jsDataBuilder = new StringBuilder();
        StringBuilder htmlMenuBuilder = new StringBuilder();

        for (String folderName : FOLDERS) {
            File folder = new File(folderName);
            
            // Intestazione sezione HTML
            htmlMenuBuilder.append("<div class='section'><h2>").append(folderName).append("</h2><div class='grid'>");

            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
                
                if (files != null) {
                    Arrays.sort(files);
                    for (File file : files) {
                        try {
                            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                            
                            // **MODIFICA CHIAVE: Sostituisci location.reload() con postMessage**
                            content = replaceReloadCalls(content);
                            
                            // PULIZIA DEL CODICE (Escape per stringhe JS)
                            content = content.replace("\\", "\\\\")       // Escape backslash
                                             .replace("`", "\\`")         // Escape backtick
                                             .replace("${", "\\${")       // Escape interpolazioni
                                             .replace("</script>", "<\\/script>"); // Escape chiusura script

                            String key = folderName + "_" + file.getName();
                            String displayName = file.getName().replace(".html", "").replace("_", " ");

                            // Aggiungiamo al JS (Salva anche il nome visuale per la barra del titolo)
                            jsDataBuilder.append("\n    // --- ").append(displayName).append(" ---\n");
                            jsDataBuilder.append("    '").append(key).append("': { name: `").append(displayName).append("`, content: `").append(content).append("` },\n");
                            
                            // Bottone HTML
                            htmlMenuBuilder.append("<button class='card' onclick=\"openPage('").append(key).append("')\">")
                                           .append(displayName)
                                           .append("</button>");
                                           
                        } catch (IOException e) {
                            System.err.println("Errore lettura: " + file.getName());
                        }
                    }
                }
            }
            htmlMenuBuilder.append("</div></div>");
        }

        generateFinalHtml(htmlMenuBuilder.toString(), jsDataBuilder.toString());
    }

    /**
     * Sostituisce tutte le chiamate a location.reload() con postMessage
     */
    private static String replaceReloadCalls(String content) {
        // Funzione helper da usare ovunque
        String reloadHelper = "window.parent.postMessage('RELOAD_REQUEST','*')";
        
        // 1. onclick="location.reload()"
        content = content.replaceAll(
            "onclick\\s*=\\s*\"\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*\"",
            "onclick=\"" + reloadHelper + "\""
        );
        
        // 2. onclick='location.reload()'
        content = content.replaceAll(
            "onclick\\s*=\\s*'\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*'",
            "onclick='" + reloadHelper + "'"
        );
        
        // 3. window.location.reload() negli script
        content = content.replaceAll(
            "window\\.location\\.reload\\s*\\(\\s*[^)]*\\s*\\)",
            reloadHelper
        );
        
        // 4. location.reload() negli script
        content = content.replaceAll(
            "(?<!window\\.)(?<!\\.)\\blocation\\.reload\\s*\\(\\s*[^)]*\\s*\\)",
            reloadHelper
        );
        
        return content;
    }

    private static void generateFinalHtml(String menuHtml, String jsObjectContent) {
        String template = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title>All pages</title>
<style>
    :root { --bg: #f4f4f9; --header-bg: #222; --text-light: #fff; }
    body { font-family: sans-serif; margin: 0; background: var(--bg); overflow-x: hidden; -webkit-tap-highlight-color: transparent; }
    
    /* --- STILE DASHBOARD --- */
    #dashboard { padding: 20px; max-width: 900px; margin: 0 auto; padding-bottom: 60px; }
    h1 { text-align: center; color: #333; margin-bottom: 30px; font-weight: 300; }
    h2 { border-bottom: 2px solid #ddd; padding-bottom: 5px; margin-top: 30px; color: #555; text-transform: uppercase; font-size: 1.1rem; }
    
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(130px, 1fr)); gap: 15px; margin-top: 15px; }
    
    .card {
        background: #fff; border: 1px solid #ddd; border-radius: 8px;
        padding: 15px; font-size: 0.9rem; font-weight: bold; color: #444;
        cursor: pointer; min-height: 60px;
        display: flex; align-items: center; justify-content: center; text-align: center;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05); transition: 0.2s;
    }
    .card:active { background: #eef; transform: scale(0.98); }

    /* --- STILE VIEWER (FLEXBOX VERTICALE) --- */
    #viewer { 
        display: none; /* Nascosto di default */
        position: fixed; top: 0; left: 0; width: 100%; height: 100%; 
        background: #fff; z-index: 1000;
        flex-direction: column; /* Chiave del layout: impila Header e Iframe */
    }
    
    /* BARRA SUPERIORE */
    #app-header {
        height: 44px; /* Altezza fissa */
        background: var(--header-bg);
        color: var(--text-light);
        display: flex; align-items: center; justify-content: space-between;
        padding: 0 15px;
        box-shadow: 0 2px 5px rgba(0,0,0,0.2);
        flex-shrink: 0; /* Impedisce che si rimpicciolisca */
        z-index: 1001;
    }
    
    #app-title { font-weight: bold; font-size: 1rem; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
    
    #close-btn {
        background: #e74c3c; color: white; border: none;
        border-radius: 4px; padding: 5px 15px;
        font-weight: bold; cursor: pointer; font-size: 0.9rem;
    }
    #close-btn:active { background: #c0392b; }

    /* IFRAME */
    #iframe-container {
        flex-grow: 1; /* Occupa tutto lo spazio rimanente sotto la barra */
        width: 100%;
        position: relative;
        background: #000; /* Sfondo nero per evitare flash bianchi */
    }
    
    iframe { width: 100%; height: 100%; border: none; display: block; }
</style>
</head>
<body>

<div id="dashboard">
    <h1>All My Pages</h1>
    {{MENU_CONTENT}}
</div>

<div id="viewer">
    <div id="app-header">
        <span id="app-title">Titolo App</span>
        <button id="close-btn" onclick="closePage()">✕</button>
    </div>
    
    <div id="iframe-container">
        <iframe id="app-frame"></iframe>
    </div>
</div>

<script>
    // --- DATABASE DATI (Readable) ---
    const pages = {
        {{JS_CONTENT}}
    };

    let currentUrl = null;
    let currentKey = null;

    function openPage(key) {
        const data = pages[key];
        if(!data) return;

        currentKey = key;

        // Aggiorna titolo barra
        document.getElementById('app-title').innerText = data.name;

        // Crea Blob URL per l'iframe
        const blob = new Blob([data.content], { type: 'text/html' });
        if (currentUrl) URL.revokeObjectURL(currentUrl);
        currentUrl = URL.createObjectURL(blob);
        
        // Attiva il layout Flexbox
        const viewer = document.getElementById('viewer');
        viewer.style.display = 'flex'; 
        
        // Blocca scroll pagina sottostante
        document.body.style.overflow = 'hidden'; 
        
        document.getElementById('app-frame').src = currentUrl;
    }

    // Ascolta messaggi di reload dall'iframe
    window.addEventListener('message', function(event) {
        if (event.data === 'RELOAD_REQUEST') {
            if (currentKey) {
                console.log('Ricarico pagina:', currentKey);
                openPage(currentKey);
            }
        }
    });

    function closePage() {
        const viewer = document.getElementById('viewer');
        viewer.style.display = 'none';
        document.body.style.overflow = '';
        document.getElementById('app-frame').src = 'about:blank';
        currentKey = null;
    }

    // Gestione tasto Indietro su Mobile
    window.history.pushState(null, null, window.location.href);
    window.onpopstate = function() {
        if(document.getElementById('viewer').style.display === 'flex') {
            window.history.pushState(null, null, window.location.href);
            closePage();
        }
    };
</script>
</body>
</html>
""";

        String finalHtml = template
                .replace("{{MENU_CONTENT}}", menuHtml)
                .replace("{{JS_CONTENT}}", jsObjectContent);

        try {
            Files.writeString(Paths.get("index.html"), finalHtml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}