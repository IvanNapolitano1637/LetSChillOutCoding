import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

//Codice per creare un unico file HTML con all'interno tutte le pagine che stanno nelle tre cartelle "Clocks", "Games" e "Tools".
//Codice creato da Gemini e Claude lunedì nove febbraio duemilaventisei dopo un altro tentativo fatto con Gemini due giorni prima.
//Ho fatto varie altre modifiche. Le ultime domenica ventinove marzo duemilaventisei.
//Il numero di pagine cresce sempre più e stanno per arrivarne diverse altre.
//Da mettere nella cartella in cui ci sono: "Clocks", "Games" e "Tools".
//Da compilare e lanciare lì da terminale.

public class HTML_Pages_Unifier {

	private static final String[] FOLDERS = {"Clocks", "Games", "Tools"};
	private static final Map<String, String> EMOJIS;
	static {
		Map<String, String> tempMap = new HashMap<>();
		tempMap.put("Clocks","🕰️");
		tempMap.put("Games","🎮");
		tempMap.put("Tools","🛠️");
		EMOJIS = Collections.unmodifiableMap(tempMap);
	}

	private static String injectEscHandler(String content) {
		String escScript = """
			<script>
			document.addEventListener('keydown', function(e) {
				if(e.key === 'Escape'){
					window.parent.postMessage('CLOSE_REQUEST', '*');
				}
			});
			</script>
			""";
		if(content.contains("</body>")){
			return content.replace("</body>", escScript + "</body>");
		}else{
			return content + escScript;
		}
	}

	public static void main(String[] args) {
		StringBuilder jsDataBuilder = new StringBuilder();
		StringBuilder htmlMenuBuilder = new StringBuilder();
		for(String folderName : FOLDERS){
			File folder = new File(folderName);
			String sectionId = "grid-" + folderName.toLowerCase();
			htmlMenuBuilder.append("<div class='section'>").append("<h2 class='section-header' tabindex='0' onclick=\"toggleSection('").append(sectionId).append("')\">").append("<span>").append(EMOJIS.get(folderName) + " " + folderName).append("</span>").append("<span class='arrow'>&#9654;</span>").append("</h2>").append("<div class='grid' id='").append(sectionId).append("'>");
			if(folder.exists() && folder.isDirectory()){
				File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".html"));
				if(files != null){
					Arrays.sort(files);
					for(File file : files){
						try{
							String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
							content = replaceReloadCalls(content);
							content = injectEscHandler(content);
							content = content.replace("\\", "\\\\").replace("`", "\\`").replace("${", "\\${").replace("</script>", "<\\/script>");
							String fileName = file.getName();
							String displayName = fileName.replace(".html", "").replace("_", " ");
							String key = folderName + "_" + fileName.replaceAll("[^a-zA-Z0-8]", "_");
							if(fileName.contains(" - ")){
								String[] parts = fileName.split(" - ");
								if(parts.length > 1){
									displayName = parts[1].replace(".html", "").replace("_", " ");
								}
							}
							jsDataBuilder.append("\n	// --- ").append(displayName).append(" ---\n");
							jsDataBuilder.append("	'").append(key).append("': { name: `").append(displayName).append("`, content: `").append(content).append("` },\n");
							htmlMenuBuilder.append("<button class='card' onclick=\"openPage('").append(key).append("')\">").append(displayName).append("</button>");
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
		content = content.replaceAll("onclick\\s*=\\s*\"\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*\"", "onclick=\"" + reloadHelper + "\"");
		content = content.replaceAll("onclick\\s*=\\s*'\\s*location\\.reload\\s*\\(\\s*[^)]*\\s*\\)\\s*'", "onclick='" + reloadHelper + "'");
		content = content.replaceAll("window\\.location\\.reload\\s*\\(\\s*[^)]*\\s*\\)", reloadHelper);
		content = content.replaceAll("(?<!window\\.)(?<!\\.)\\blocation\\.reload\\s*\\(\\s*[^)]*\\s*\\)", reloadHelper);
		return content;
	}

	private static void generateFinalHtml(String menuHtml, String jsObjectContent){
		String template = """
<!DOCTYPE html>
<!--https://github.com/IvanNapolitano1637/LetSChillOutCoding/blob/main/HTML_CSS_JavaScript/index.html-->
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Toolbox & Playground</title>
<style>
	:root{
		--bg-gradient: linear-gradient(-45deg, #1e2a38, #11151c, #1a222c, #0f1218);
		--card-bg: rgba(255, 255, 255, 0.05);
		--card-border: rgba(255, 255, 255, 0.1);
		--text-main: #ffffff;
		--accent: #4a90e2;
		--accent-hover: #357abd;
		--viewer-bg: rgba(20, 20, 25, 0.98);
	}

	*{
		box-sizing: border-box;
		-webkit-tap-highlight-color: transparent;
	}

	*:focus{
		outline: none !important;
		box-shadow: none !important;
	}

	body{ 
		font-family: 'Segoe UI', Roboto, sans-serif; 
		margin: 0; 
		background: var(--bg-gradient); 
		background-size: 400% 400%;
		animation: gradientBG 15s ease infinite;
		color: var(--text-main); 
		min-height: 100vh; 
		overflow-x: hidden; 
	}
	
	@keyframes gradientBG {
		0% {
			background-position: 0% 50%;
			}
		50% {
			background-position: 100% 50%;
			}
		100% {
			background-position: 0% 50%;
			}
	}

	#dashboard{
		padding: 40px 20px;
		max-width: 1000px;
		margin: 0 auto;
		animation: fadeIn 1s ease-out;
	}
	
	h1{ 
		text-align: center;
		font-weight: 200;
		font-size: 2.5rem;
		letter-spacing: 3px;
		margin-bottom: 50px; 
		background: linear-gradient(to right, #fff, #888);
		-webkit-background-clip: text;
		background-clip: text; 
		-webkit-text-fill-color: transparent;
		-webkit-tap-highlight-color: transparent;
	}

	h2{ 
		border-bottom: 1px solid var(--card-border);
		padding-bottom: 10px;
		margin-top: 50px; 
		color: var(--accent);
		font-size: 0.9rem;
		text-transform: uppercase;
		letter-spacing: 1.5px; 
		transition: color 0.3s;
	}

	.section-header{ 
		cursor: pointer;
		display: flex;
		align-items: center;
		justify-content: space-between; 
		-webkit-tap-highlight-color: transparent;
		transition: 0.3s;
	}

	.section-header:hover{
		color: var(--text-main);
	}

	.section-header .arrow{
		font-size: 0.7rem;
		transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
	}

	.section-header.open .arrow{
		transform: rotate(90deg);
		color: var(--accent);
	}

	.grid{
		display: none;
		grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
		gap: 20px;
		padding-top: 15px;
	}

	.card{
		background: var(--card-bg);
		border: 1px solid var(--card-border);
		border-radius: 12px;
		padding: 20px;
		color: var(--text-main);
		cursor: pointer;
		min-height: 100px;
		display: flex;
		align-items: center;
		justify-content: center;
		text-align: center;
		transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
		-webkit-backdrop-filter: blur(5px);
		backdrop-filter: blur(5px);
		-webkit-user-select: none;
		user-select: none;
	}

	.card:hover{ 
		transform: translateY(-8px) scale(1.03); 
		background: rgba(255, 255, 255, 0.08);
		border-color: var(--accent);
		box-shadow: 0 10px 25px rgba(74, 144, 226, 0.25);
	}

	.card:active{
		transform: translateY(-2px) scale(0.98);
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
		-webkit-backdrop-filter: blur(5px);
		backdrop-filter: blur(20px);
		transition: opacity 0.3s ease;
	}

	.viewer-show{
		animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards;
	}

	@keyframes slideUp{
		from{
			transform: translateY(30px);
			opacity: 0;
		}
		to{
			transform: translateY(0);
			opacity: 1;
		}
	}

	#app-header{ 
		height: 60px;
		background: rgba(0,0,0,0.4);
		border-bottom: 1px solid var(--card-border); 
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 25px;
		flex-shrink: 0; 
	}
	
	#app-title{
		font-weight: 300;
		letter-spacing: 1px;
		font-size: 1.1rem;
		color: var(--accent);
	}

	#close-btn{ 
		background: rgba(255,255,255,0.05);
		color: #fff;
		border: 1px solid rgba(255,255,255,0.2); 
		border-radius: 30px;
		padding: 8px 20px;
		cursor: pointer;
		transition: 0.3s; 
		text-transform: uppercase;
		font-size: 0.75rem;
		font-weight: bold;
		letter-spacing: 1px;
	}

	#close-btn:hover{
		background: #e74c3c;
		border-color: #e74c3c;
		box-shadow: 0 0 15px rgba(231, 76, 60, 0.4);
	}

	#iframe-container{
		flex-grow: 1;
		background: #fff;
		overflow: hidden;
	}

	iframe{
		width: 100%;
		height: 100%;
		border: none;
		display: block;
	}

	@keyframes fadeIn{
		from{
			opacity: 0;
		}to{
			opacity: 1;
		}
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
		<iframe id="app-frame" name="app-frame" tabindex="0"></iframe>
	</div>
</div>

<script>
	const pages = { {{JS_CONTENT}} };
	let currentKey = null;

	function toggleSection(gridId){
		const grid = document.getElementById(gridId);
		const header = grid.previousElementSibling;
		const isOpen = grid.style.display === 'grid';
		grid.style.display = isOpen ? 'none' : 'grid';
		header.classList.toggle('open', !isOpen);
	}

	function openPage(key){
		const data = pages[key];
		if(!data){
			return;
		}
		currentKey = key;
		document.getElementById('app-title').innerText = data.name;
		const viewer = document.getElementById('viewer');
		const frame = document.getElementById('app-frame');
		viewer.style.display = 'flex';
		viewer.classList.add('viewer-show');
		document.body.style.overflow = 'hidden';
		frame.srcdoc = data.content;
		frame.onload = function() {
			frame.focus();
		};
	}

	window.addEventListener('message', function(event){
		if(event.data === 'RELOAD_REQUEST' && currentKey){
			openPage(currentKey);
		}
		if(event.data === 'CLOSE_REQUEST'){
			closePage();
		}
	});

	function closePage(){
		const viewer = document.getElementById('viewer');
		viewer.style.opacity = '0';
		setTimeout(() => {
			viewer.style.display = 'none';
			viewer.style.opacity = '1';
			viewer.classList.remove('viewer-show');
			document.body.style.overflow = '';
			document.getElementById('app-frame').srcdoc = '';
		}, 300);
		currentKey = null;
	}

	document.addEventListener('keydown', function(e){
		if(e.key === 'Escape'){
			closePage();
		}
		if(document.activeElement.tagName !== 'INPUT' && document.activeElement.tagName !== 'TEXTAREA'){
			if(e.key.toLowerCase() === 'c'){
				toggleSection('grid-clocks');
			}
			if(e.key.toLowerCase() === 'g'){
				toggleSection('grid-games');
			}
			if(e.key.toLowerCase() === 't'){
				toggleSection('grid-tools');
			}
		}
	});
</script>
</body>
</html>
""";
		String finalHtml = template.replace("{{MENU_CONTENT}}", menuHtml).replace("{{JS_CONTENT}}", jsObjectContent);
		try{
			Files.writeString(Paths.get("index.html"), finalHtml);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}