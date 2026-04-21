package me.cortex.voxy.client.core.gl.shader;

import net.caffeinemc.mods.sodium.client.gl.shader.ShaderConstants;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderParser;

import java.io.InputStream;
import java.util.Scanner;

public class ShaderLoader {
    public static String parse(String id) {
        String source = getShaderSource(id);
        source = preprocessShaderImports(source);
        return "#version 460 core\n"
                + ShaderParser.parseShader("\n" + source + "\n//beans", ShaderConstants.builder().build())
                .replaceAll("\r\n", "\n")
                .replaceFirst("\n#version .+\n", "\n");
    }

    private static String preprocessShaderImports(String source) {
        StringBuilder result = new StringBuilder();
        for (String line : source.split("\n")) {
            String t = line.trim();
            if (t.startsWith("#import")) {
                int lt = t.indexOf('<'), gt = t.lastIndexOf('>');
                if (lt < 0 || gt < lt) {
                    result.append(line).append('\n');
                    continue;
                }
                String importPath = t.substring(lt + 1, gt).trim();
                String imported = getShaderSource(importPath);
                imported = stripDirectivesFromImport(imported);
                imported = preprocessShaderImports(imported);
                result.append(imported).append('\n');
            } else {
                result.append(line).append('\n');
            }
        }
        return result.toString();
    }

    private static String stripDirectivesFromImport(String src) {
        StringBuilder sb = new StringBuilder();
        for (String line : src.split("\n")) {
            String t = line.trim();
            if (t.startsWith("#version") || t.startsWith("#extension")) {
                sb.append('\n');
            } else {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    private static String getShaderSource(String id) {
        String resourcePath;
        if (id.startsWith("/assets/")) {
            resourcePath = id;
        } else {
            int colon = id.indexOf(':');
            if (colon < 0) {
                resourcePath = "/assets/voxy/shaders/" + id;
            } else {
                resourcePath = "/assets/" + id.substring(0, colon) + "/shaders/" + id.substring(colon + 1);
            }
        }
        try (InputStream is = ShaderLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Shader not found: " + resourcePath);
            }
            try (Scanner sc = new Scanner(is).useDelimiter("\\A")) {
                return sc.hasNext() ? sc.next() : "";
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + id, e);
        }
    }
}
