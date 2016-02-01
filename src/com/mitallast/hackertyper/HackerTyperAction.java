package com.mitallast.hackertyper;

import com.intellij.codeInsight.template.impl.editorActions.TypedActionHandlerBase;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import sun.util.logging.PlatformLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HackerTyperAction extends TypedActionHandlerBase {
    private static final PlatformLogger logger = PlatformLogger.getLogger("com.mitallast.hackertyper.HackerTyperAction");

    private static final String javaSourceCode;
    private static final String phpSourceCode;

    static {
        javaSourceCode = loadSource("java_source_code.txt");
        phpSourceCode = loadSource("php_source_code.txt");
    }

    public HackerTyperAction(TypedActionHandler typedActionHandler) {
        super(typedActionHandler);
    }

    @Override
    public void execute(Editor editor, char c, DataContext dataContext) {
        final Document document = editor.getDocument();
        final VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        if (virtualFile != null) {
            logger.info("virtual file type: " + virtualFile.getFileType().getName());

            int offset = editor.getCaretModel().getOffset();

            switch (virtualFile.getFileType().getName()) {
                case "JAVA":
                    c = nextJavaSymbol(offset);
                    break;
                case "PHP":
                    c = nextPhpSymbol(offset);
                    break;
            }
        }

        if (myOriginalHandler != null) {
            myOriginalHandler.execute(editor, c, dataContext);
        }
    }

    private char nextJavaSymbol(int offset) {
        int next = offset % javaSourceCode.length();
        return javaSourceCode.charAt(next);
    }

    private char nextPhpSymbol(int offset) {
        int next = offset % phpSourceCode.length();
        return phpSourceCode.charAt(next);
    }

    private static String loadSource(String path) {
        String fileContent = "default content";
        try (InputStream resource = HackerTyperAction.class.getResourceAsStream(path)) {
            fileContent = new BufferedReader(new InputStreamReader(resource))
                .lines()
                .reduce((s, s2) -> s + "\n" + s2)
                .get();
        } catch (IOException e) {
            logger.warning("error load resource", e);
        }
        return fileContent;
    }
}
