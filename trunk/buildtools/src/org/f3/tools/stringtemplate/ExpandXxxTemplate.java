package org.f3.tools.stringtemplate;

import java.io.*;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 * RunStringTemplate
 *
 * @author Brian Goetz
 */
public class ExpandXxxTemplate {

    private static final String[] keys = { "Int", "Double", "Float", "Short", "Char", "Long", "Boolean", "Byte" };
    private static final String[] boxed = { "Integer", "Double", "Float", "Short", "Character", "Long", "Boolean", "Byte" };
    private static final String[] prims = { "int", "double", "float", "short", "char", "long", "boolean", "byte" };

    /**
     * Usage: ExpandXxxTemplate dest-root relative-source-path template-name...
     */
    public static void main(String[] args) throws IOException {
        File destDir = new File(args[0]);
        String stgName = args[1];
        InputStream stream = ExpandXxxTemplate.class.getClassLoader().getResourceAsStream(stgName);
        if (stream == null)
            throw new RuntimeException("Cannot find " + stgName + " on class path");
        StringTemplateGroup stg = new StringTemplateGroup(new InputStreamReader(stream));
        String sourcePath = args[2];
        StringTemplateGroup loader = new StringTemplateGroup("Xxx");
        for (int i=3; i<args.length; i++) {
            String templateName = args[i];
            File outputDir = new File(destDir, sourcePath);
            outputDir.mkdirs();
            if (templateName.indexOf("Xxx") < 0) {
                File outFile = new File(outputDir, templateName + ".java");
                if (outFile.exists())
                    continue;
                StringTemplate st = loader.getInstanceOf(sourcePath + File.separator + templateName);
                st.setGroup(stg);
                st.setAttribute("PREFIX", keys);
                st.setAttribute("BOXED", boxed);
                st.setAttribute("PRIM", prims);
                //st.setAttribute("NUMERIC", !k.equals("Boolean") && !k.equals("Char"));
                st.setAttribute("TEMPLATE_NAME", templateName);
                st.setAttribute("TEMPLATE_FROM",
                                "/*\n * WARNING: Automatically generated from the template "+templateName+".st; do not edit!\n */");
                Writer out = new FileWriter(outFile);
                out.write(st.toString());
                out.close();
                continue;
            }
            for (String k : keys) {
                String outName = templateName.replace("Xxx", k);
                File outFile = new File(outputDir, outName + ".java");
                if (outFile.exists())
                    continue;
                StringTemplate st = loader.getInstanceOf(sourcePath + File.separator + templateName);
                st.setGroup(stg);
                st.setAttribute("PREFIX", k);
                st.setAttribute("NUMERIC", !k.equals("Boolean") && !k.equals("Char"));
                st.setAttribute("TEMPLATE_NAME", templateName);
                Writer out = new FileWriter(outFile);
                out.write(st.toString());
                out.close();
            }
        }
    }
}
