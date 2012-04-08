package org.f3.ideaplugin.parsing;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.f3.api.F3cTask;
import org.f3.api.tree.UnitTree;
import org.f3.ideaplugin.F3Compiler;
import org.f3.tools.api.F3cTool;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author David Kaspar
*/
public class F3Annotator implements ExternalAnnotator {

    public void annotate (final PsiFile file, final AnnotationHolder holder) {
        if (file == null)
            return;
        final VirtualFile virtualFile = file.getVirtualFile ();
        Module module = VfsUtil.getModuleForFile (file.getProject (), virtualFile);
        if (module == null)
            return;

        F3cTool instance = new F3cTool();
        DiagnosticListener<FileObject> diagnosticListener = new DiagnosticListener<FileObject>() {
            public void report (Diagnostic<? extends FileObject> diagnostic) {
                switch (diagnostic.getKind ()) {
                    case ERROR:
                        holder.createErrorAnnotation (new TextRange ((int) diagnostic.getStartPosition (), (int) diagnostic.getEndPosition ()), diagnostic.getMessage (Locale.getDefault ()));
                        break;
                    case MANDATORY_WARNING:
                    case WARNING:
                        holder.createWarningAnnotation (new TextRange ((int) diagnostic.getStartPosition (), (int) diagnostic.getEndPosition ()), diagnostic.getMessage (Locale.getDefault ()));
                        break;
                    default:
                        holder.createInfoAnnotation (new TextRange ((int) diagnostic.getStartPosition (), (int) diagnostic.getEndPosition ()), diagnostic.getMessage (Locale.getDefault ()));
                        break;
                }
            }
        };

        StandardJavaFileManager fileManager = instance.getStandardFileManager(diagnosticListener, null, null);


        URI uri = URI.create (virtualFile.getUrl ());
        JavaFileObject compiledFile = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {
            public URI toUri () {
                return uri;
            }
            public Kind getKind () {
                return Kind.SOURCE;
            }
            public CharSequence getCharContent (boolean ignoreEncodingErrors) throws IOException {
                return file.getText ();
            }
        };

        List<String> commandLine = F3Compiler.createCommandLine (ModuleRootManager.getInstance (module));

        try {
            F3cTask task = instance.getTask(null, fileManager, diagnosticListener, commandLine, Collections.singleton (compiledFile));
            Iterable<? extends UnitTree> result1 = task.parse();
            Iterable<? extends UnitTree> result2 = task.analyze();
        } catch (IOException e) {
            e.printStackTrace ();  // TODO
        }
//        UnitTree t = result2.iterator().next();
    }

}
