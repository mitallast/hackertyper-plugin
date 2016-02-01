package com.mitallast.hackertyper;

import com.intellij.codeInsight.template.impl.editorActions.TypedActionHandlerBase;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import sun.util.logging.PlatformLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class HackerTyperAction extends TypedActionHandlerBase {
    private static final PlatformLogger logger = PlatformLogger.getLogger("com.mitallast.hackertyper.HackerTyperAction");

    private static final String sourceCode;
    private static final AtomicInteger sourcePointer = new AtomicInteger();

    static {
        String fileContent = "default content";
        try (InputStream resource = HackerTyperAction.class.getResourceAsStream("source_code.txt")) {
            fileContent = new BufferedReader(new InputStreamReader(resource))
                .lines()
                .reduce((s, s2) -> s + "\n" + s2)
                .get();
        } catch (Throwable e) {
            logger.warning("error load resource", e);
        } finally {
            sourceCode = fileContent;
        }
    }

    public HackerTyperAction(TypedActionHandler typedActionHandler) {
        super(typedActionHandler);
    }

    @Override
    public void execute(Editor editor, char c, DataContext dataContext) {
        logger.info("editor " + editor + " char " + c + " data context " + dataContext);

        int next = sourcePointer.getAndIncrement() % sourceCode.length();
        c = sourceCode.charAt(next);

        if (myOriginalHandler != null) {
            myOriginalHandler.execute(editor, c, dataContext);
        }
    }
}
