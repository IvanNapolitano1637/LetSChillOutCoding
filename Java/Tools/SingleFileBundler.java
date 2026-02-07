import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SingleFileBundler {

    private static final String SOURCE_DIR = System.getProperty("user.dir");
    private static final String OUTPUT_FILE = "All_pages.html";
    private static final String[] CATEGORIES = {"Clocks", "Games", "Tools"};

    public static void main(String[] args) {
        try {
            Path sourcePath = Paths.get(SOURCE_DIR);
            StringBuilder jsDataBuilder = new StringBuilder();
            jsDataBuilder.append("const appLibrary = {\n");
            boolean first = true;
            for (String category : CATEGORIES) {
                Path catPath = sourcePath.resolve(category);
                if (Files.exists(catPath) && Files.isDirectory(catPath)) {
                    List<Path> files;
                    try (Stream<Path> walk = Files.list(catPath)) {
                        files = walk.filter(p -> p.toString().toLowerCase().endsWith(".html"))
                                   .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                                   .collect(Collectors.toList());
                    }
                    for (Path file : files) {
                        if (!first) jsDataBuilder.append(",\n");
                        String key = category + "|" + file.getFileName().toString();
                        String base64Content = encodeFileToBase64(file);
                        jsDataBuilder.append("    \"").append(key).append("\": \"").append(base64Content).append("\"");
                        first = false;
                    }
                }
            }
            jsDataBuilder.append("\n};");
            String finalHtml = buildHtmlStructure(jsDataBuilder.toString(), sourcePath);
            Path outputPath = sourcePath.resolve(OUTPUT_FILE);
            Files.write(outputPath, finalHtml.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String encodeFileToBase64(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String buildHtmlStructure(String jsLibrary, Path sourcePath) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n");
        html.append("<title>All-in-One Collection</title>\n");
        html.append("<style>\n");
        html.append("  * { box-sizing: border-box; margin: 0; padding: 0; }\n");
        html.append("  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: #f4f4f9; height: 100vh; overflow: hidden; }\n");
        html.append("  #menu-container { height: 100%; overflow-y: auto; padding: 20px; transition: transform 0.3s ease; }\n");
        html.append("  h1 { text-align: center; margin-bottom: 30px; color: #333; }\n");
        html.append("  .category-group { margin-bottom: 30px; background: white; border-radius: 10px; padding: 15px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }\n");
        html.append("  .cat-title { font-size: 1.2rem; color: #007bff; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 15px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; }\n");
        html.append("  .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 10px; }\n");
        html.append("  .app-btn { background: #f8f9fa; border: 1px solid #ddd; padding: 15px; text-align: center; border-radius: 8px; cursor: pointer; transition: all 0.2s; font-size: 0.9rem; font-weight: 600; color: #444; }\n");
        html.append("  .app-btn:hover { background: #007bff; color: white; transform: translateY(-2px); shadow: 0 4px 8px rgba(0,0,0,0.1); }\n");
        html.append("  #app-viewer { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: white; transform: translateX(100%); transition: transform 0.3s ease; z-index: 1000; display: flex; flex-direction: column; }\n");
        html.append("  #app-viewer.active { transform: translateX(0); }\n");
        html.append("  #top-bar { height: 50px; background: #222; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; color: white; flex-shrink: 0; }\n");
        html.append("  #app-title-display { font-size: 1rem; font-weight: normal; opacity: 0.8; }\n");
        html.append("  #close-btn { font-size: 30px; line-height: 1; cursor: pointer; color: #fff; font-weight: bold; user-select: none; }\n");
        html.append("  #close-btn:hover { color: #ff6b6b; }\n");
        html.append("  iframe { border: none; flex-grow: 1; width: 100%; height: 100%; background: #fff; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        html.append("<div id=\"menu-container\">\n");
        html.append("  <h1>My Collection</h1>\n");
        for (String cat : CATEGORIES) {
            Path catPath = sourcePath.resolve(cat);
            if (Files.exists(catPath) && Files.isDirectory(catPath)) {
                html.append("  <div class=\"category-group\">\n");
                html.append("    <div class=\"cat-title\">").append(cat).append("</div>\n");
                html.append("    <div class=\"grid\">\n");
                try (Stream<Path> walk = Files.list(catPath)) {
                   List<String> fileNames = walk.filter(p -> p.toString().toLowerCase().endsWith(".html"))
                           .map(p -> p.getFileName().toString())
                           .sorted()
                           .collect(Collectors.toList());
                   if (fileNames.isEmpty()) {
                       html.append("<div>No files found</div>");
                   }
                   for (String fName : fileNames) {
                       String key = cat + "|" + fName;
                       String cleanName = fName.replace(".html", "").replace("_", " ");
                       html.append("      <div class=\"app-btn\" onclick=\"openApp('").append(key).append("', '").append(cleanName).append("')\">")
                           .append(cleanName).append("</div>\n");
                   }
                } catch (IOException e) { e.printStackTrace(); }
                html.append("    </div>\n");
                html.append("  </div>\n");
            }
        }
        html.append("</div>\n");
        html.append("<div id=\"app-viewer\">\n");
        html.append("  <div id=\"top-bar\">\n");
        html.append("    <span id=\"app-title-display\"></span>\n");
        html.append("    <span id=\"close-btn\" onclick=\"closeApp()\">&times;</span>\n");
        html.append("  </div>\n");
        html.append("  <iframe id=\"content-frame\"></iframe>\n");
        html.append("</div>\n");
        html.append("<script>\n");
        html.append(jsLibrary).append("\n"); 
        html.append("\n");
        html.append("  const viewer = document.getElementById('app-viewer');\n");
        html.append("  const iframe = document.getElementById('content-frame');\n");
        html.append("  const titleDisplay = document.getElementById('app-title-display');\n");
        html.append("\n");
        html.append("  function openApp(key, name) {\n");
        html.append("    if (appLibrary[key]) {\n");
        html.append("      // Decodifica il contenuto Base64\n");
        html.append("      const content = atob(appLibrary[key]);\n");
        html.append("      titleDisplay.textContent = name;\n");
        html.append("      viewer.classList.add('active');\n");
        html.append("      // Inietta il contenuto nell'iframe\n");
        html.append("      iframe.src = 'data:text/html;charset=utf-8;base64,' + appLibrary[key];\n");
        html.append("    } else {\n");
        html.append("      alert('Error loading page.');\n");
        html.append("    }\n");
        html.append("  }\n");
        html.append("\n");
        html.append("  function closeApp() {\n");
        html.append("    viewer.classList.remove('active');\n");
        html.append("    // Pulisci l'iframe per fermare eventuali suoni o loop di gioco\n");
        html.append("    setTimeout(() => { iframe.src = 'about:blank'; }, 300);\n");
        html.append("  }\n");
        html.append("</script>\n");
        html.append("</body>\n</html>");
        return html.toString();
    }
}