import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

//Codice per creare un unico file HTML con all'interno tutte le pagine che stanno nelle tre cartelle "Clocks", "Games" e "Tools".
//Codice creato da Gemini e Claude lunedì nove febbraio duemilaventisei dopo un altro tentativo fatto con Gemini due giorni prima.
//Ho fatto varie altre modifiche. L'ultima domenica ventidue febbraio.
//Questa per far comparire le pagine in ordine cronologico di mia creazione.
//L'ordine è basato sul codice anteposto al nome del file quindi è customizzabile.
//Unica differenza tra le pagine originali e quelle contenute nel file creato qui è una sezione con un pulsante per tornare all'indice di tutte le pagine.
//Da mettere nella cartella in cui ci sono: "Clocks", "Games" e "Tools".
//Da compilare e lanciare lì da terminale.

public class HTML_Pages_Unifier{

	private static final String[] FOLDERS = {"Clocks", "Games", "Tools"};
	private static final Map<String, String> EMOJIS;
	static {
		Map<String, String> tempMap = new HashMap<>();
		tempMap.put("Clocks","🕰️");
		tempMap.put("Games","🎮");
		tempMap.put("Tools","🛠️");
		EMOJIS = Collections.unmodifiableMap(tempMap);
	}

	public static void main(String[] args) {
		StringBuilder jsDataBuilder = new StringBuilder();
		StringBuilder htmlMenuBuilder = new StringBuilder();
		for(String folderName : FOLDERS){
			File folder = new File(folderName);
			htmlMenuBuilder.append("<div class='section'><h2>").append(EMOJIS.get(folderName) + " " + folderName).append("</h2><div class='grid'>");
			if(folder.exists() && folder.isDirectory()){
				File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
				if(files != null){
					Arrays.sort(files);
					for(File file : files){
						try{
							String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
							content = replaceReloadCalls(content);
							content = content.replace("\\", "\\\\")
											 .replace("`", "\\`")
											 .replace("${", "\\${")
											 .replace("</script>", "<\\/script>");
							String key = folderName + "_" + file.getName().split(" - ")[1];
							String displayName = (file.getName().split(" - ")[1]).replace(".html", "").replace("_", " ");
							jsDataBuilder.append("\n	// --- ").append(displayName).append(" ---\n");
							jsDataBuilder.append("	'").append(key).append("': { name: `").append(displayName).append("`, content: `").append(content).append("` },\n");
							htmlMenuBuilder.append("<button class='card' onclick=\"openPage('").append(key).append("')\">")
										   .append(displayName)
										   .append("</button>");
						}catch(IOException e){
							System.err.println("Errore lettura: " + file.getName());
						}
					}
				}
			}
			htmlMenuBuilder.append("</div></div>");
		}
		generateFinalHtml(htmlMenuBuilder.toString(), jsDataBuilder.toString());
	}

	private static String replaceReloadCalls(String content){
		String reloadHelper = "window.parent.postMessage('RELOAD_REQUEST','*')";
		content = content.replaceAll(
			"onclick\\s*=\\s*\"\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*\"",
			"onclick=\"" + reloadHelper + "\""
		);
		content = content.replaceAll(
			"onclick\\s*=\\s*'\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*'",
			"onclick='" + reloadHelper + "'"
		);
		content = content.replaceAll(
			"window\\.location\\.reload\\s*\\(\\s*[^)]*\\s*\\)",
			reloadHelper
		);
		content = content.replaceAll(
			"(?<!window\\.)(?<!\\.)\\blocation\\.reload\\s*\\(\\s*[^)]*\\s*\\)",
			reloadHelper
		);
		return content;
	}

	private static void generateFinalHtml(String menuHtml, String jsObjectContent){
		String template = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title>Toolbox & Playground</title>
<style>
	:root{
		--bg-gradient: linear-gradient(135deg, #1e2a38 0%, #11151c 100%);
		--card-bg: rgba(255, 255, 255, 0.05);
		--card-border: rgba(255, 255, 255, 0.1);
		--text-main: #ffffff;
		--text-sec: #a0a0b0;
		--accent: #4a90e2;
		--accent-hover: #357abd;
		--viewer-bg: rgba(20, 20, 25, 0.95);
	}

	*{
		box-sizing: border-box;
	}

	body{
		font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
		margin: 0;
		background: var(--bg-gradient);
		color: var(--text-main);
		min-height: 100vh;
		overflow-x: hidden;
		-webkit-tap-highlight-color: transparent;
	}

	::-webkit-scrollbar {
		width: 8px;
	}

	::-webkit-scrollbar-track{
		background: #111;
	}

	::-webkit-scrollbar-thumb{
		background: #444;
		border-radius: 4px;
	}

	::-webkit-scrollbar-thumb:hover{
		background: var(--accent);
	}

	#dashboard{
		padding: 40px 20px;
		max-width: 1000px;
		margin: 0 auto;
		animation: fadeIn 0.8s ease-out;
	}

	h1{
		text-align: center;
		font-weight: 200;
		font-size: 2.5rem;
		letter-spacing: 2px;
		margin-bottom: 50px;
		background: linear-gradient(to right, #fff, #aaa);
		-webkit-background-clip: text;
		background-clip: text;
		-webkit-text-fill-color: transparent;
		-webkit-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

	h2{
		border-bottom: 1px solid var(--card-border);
		padding-bottom: 10px;
		margin-top: 50px;
		margin-bottom: 20px;
		color: var(--accent);
		font-size: 0.9rem;
		text-transform: uppercase;
		letter-spacing: 1.5px;
		-webkit-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

	.grid{
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
		gap: 20px;
	}

	.card{
		background: var(--card-bg);
		border: 1px solid var(--card-border);
		border-radius: 12px;
		padding: 20px;
		font-size: 1rem;
		color: var(--text-main);
		cursor: pointer;
		min-height: 100px;
		display: flex;
		align-items: center;
		justify-content: center;
		text-align: center;
		transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
		-webkit-backdrop-filter: blur(5px);
		backdrop-filter: blur(5px);
		box-shadow: 0 4px 6px rgba(0,0,0,0.1);
		-webkit-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

	.card:hover{
		transform: translateY(-5px) scale(1.02);
		-webkit-backdrop-filter: blur(5px);
		backdrop-filter: blur(5px);
		box-shadow: 0 4px 6px rgba(0,0,0,0.1);
		-webkit-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

	.card:active{
		transform: scale(0.95);
	}

	#viewer{
		display: none;
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background: var(--viewer-bg);
		z-index: 2000;
		flex-direction: column;
		-webkit-backdrop-filter: blur(15px);
		backdrop-filter: blur(15px);
		animation: slideUp 0.3s ease-out;
	}

	@keyframes slideUp{
		from{
			transform: translateY(100%);
			opacity: 0;
		}to{
			transform: translateY(0);
			opacity: 1;
		}
	}

	@keyframes fadeIn{
		from{
			opacity: 0;
		}to{
			opacity: 1;
		}
	}

	#app-header{
		height: 50px;
		background: rgba(0,0,0,0.3);
		border-bottom: 1px solid var(--card-border);
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 20px;
		flex-shrink: 0;
	}

	#app-title{
		font-weight: 500;
		letter-spacing: 0.5px;
		font-size: 0.95rem;
		color: #fff;
	}

	#close-btn{
		background: transparent;
		color: #fff;
		border: 1px solid rgba(255,255,255,0.3);
		border-radius: 20px;
		padding: 6px 16px;
		font-size: 0.8rem;
		cursor: pointer;
		transition: 0.2s;
		text-transform: uppercase;
		font-weight: bold;
	}

	#close-btn:hover{
		background: #e74c3c;
		border-color: #e74c3c;
	}

	#iframe-container{
		flex-grow: 1;
		width: 100%;
		position: relative;
		background: #fff;
	}

	iframe{
		width: 100%;
		height: 100%;
		border: none;
		display: block;
	}
</style>
</head>
<body>
<div id="dashboard">
	<h1>Toolbox & Playground</h1>
	{{MENU_CONTENT}}
</div>
<div id="viewer">
	<div id="app-header">
		<span id="app-title">App Title</span>
		<button id="close-btn" onclick="closePage()">Close</button>
	</div>
	<div id="iframe-container">
		<iframe id="app-frame"></iframe>
	</div>
</div>

<script>
	const pages = {
		{{JS_CONTENT}}
	};

	let currentUrl = null;
	let currentKey = null;

	function openPage(key){
		const data = pages[key];
		if(!data){
			return;
		}
		currentKey = key;
		document.getElementById('app-title').innerText = data.name;
		const blob = new Blob([data.content], { type: 'text/html' });
		if(currentUrl){
			URL.revokeObjectURL(currentUrl);
		}
		currentUrl = URL.createObjectURL(blob);
		const viewer = document.getElementById('viewer');
		viewer.style.display = 'flex'; 
		document.body.style.overflow = 'hidden'; 
		document.getElementById('app-frame').src = currentUrl;
	}

	window.addEventListener('message', function(event){
		if(event.data === 'RELOAD_REQUEST'){
			if(currentKey){
				openPage(currentKey);
			}
		}
	});

	function closePage(){
		const viewer = document.getElementById('viewer');
		viewer.style.display = 'none';
		document.body.style.overflow = '';
		document.getElementById('app-frame').src = 'about:blank';
		currentKey = null;
	}

	window.history.pushState(null, null, window.location.href);
	window.onpopstate = function(){
		if(document.getElementById('viewer').style.display === 'flex'){
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
		try{
			Files.writeString(Paths.get("index.html"), finalHtml);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}