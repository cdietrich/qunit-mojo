package com.cj.qunit.mojo.http;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.httpobjects.HttpObject;
import org.httpobjects.Response;

class AutoGeneratedQunitHtmlWrappers extends HttpObject {
    private static final String TEMPLATE = readClasspathResource("/qunit.template.html");
    private final String webPathToRequireDotJsConfig;
    private final String webRoot;
    
    public AutoGeneratedQunitHtmlWrappers(String webRoot, String webPathToRequireDotJsConfig) {
        super("/{resource*}");
        this.webPathToRequireDotJsConfig = webPathToRequireDotJsConfig==null?"/qunit-mojo/default-require-config.js":webPathToRequireDotJsConfig;
        this.webRoot = webRoot;
    }

    private String createConfigTag(String configPath) {
        if (!configPath.trim().equals("")) {
            return "<script src='" + configPath + "' type='text/javascript'></script>";
        } else {
            return "";
        }
    }
    
    public Response get(org.httpobjects.Request req) {
        final String path = req.path().valueFor("resource");
       
        boolean isJs = path.endsWith(".qunit.js.Qunit.html");
        boolean isCoffee = path.endsWith(".qunit.coffee.Qunit.html");
               
        String impliedJavascriptFile;
        if(isJs)
            impliedJavascriptFile = path.replaceAll(Pattern.quote(".js.Qunit.html"), "");
        else if(isCoffee)
            impliedJavascriptFile = "cs!" + path.replaceAll(Pattern.quote(".coffee.Qunit.html"), "");
        else
            return null;

        final String fileName = lastPathSegment(impliedJavascriptFile);
        
        final String generatedHtmlFileContent = TEMPLATE.
        											replaceAll("YOUR_JAVASCRIPT_TEST_FILE_GOES_HERE", Matcher.quoteReplacement(impliedJavascriptFile)).
        											replaceAll("YOUR_REQUIRE_DOT_JS_CONFIG_GOES_HERE", createConfigTag(Matcher.quoteReplacement(webPathToRequireDotJsConfig))).
        											replaceAll("YOUR_JAVASCRIPT_TEST_FILE_NAME_GOES_HERE", fileName);


        return OK(Html(generatedHtmlFileContent));
    }

    private String lastPathSegment(final String impliedJavascriptFile) {
        final String[] parts = impliedJavascriptFile.split("/");
        
        final String fileName = parts.length == 0 ? impliedJavascriptFile : parts[parts.length-1];
        return fileName;
    }
    
    private static String readClasspathResource(String name){
        try {
            return IOUtils.toString(AutoGeneratedQunitHtmlWrappers.class.getResourceAsStream(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}