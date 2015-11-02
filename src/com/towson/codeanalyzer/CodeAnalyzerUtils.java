package com.towson.codeanalyzer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.*;
import java.util.*;

public final class CodeAnalyzerUtils {

    // Get hash values of project
    public static Map<String, Set<VirtualFile>> getVirtualFiles(Project project)
    {
        Map<String, Set<VirtualFile>> result = new HashMap<String, Set<VirtualFile>>();
        VirtualFile baseDir = project.getBaseDir();

        if (baseDir != null)
        {
            VirtualFile[] children = baseDir.getChildren();
            Set<VirtualFile> filesTobeMerged = new HashSet<VirtualFile>(Arrays.asList(children));

            Set<VirtualFile> modulesFilesTobeMerged = new HashSet<VirtualFile>();
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules)
            {
                VirtualFile moduleFile = module.getModuleFile();
                if (moduleFile == null)
                    continue;
                modulesFilesTobeMerged.add(moduleFile.getParent());
            }

            Set<VirtualFile> allFilesTobeMerged = new HashSet<VirtualFile>();
            allFilesTobeMerged.addAll(filesTobeMerged);
            allFilesTobeMerged.addAll(modulesFilesTobeMerged);

            result.putAll(getVirtualFiles((VirtualFile[])allFilesTobeMerged.toArray(new VirtualFile[allFilesTobeMerged.size()])));
        }
        return result;
    }

    // Get hash keys and values pairs of virtual files
    public static Map<String, Set<VirtualFile>> getVirtualFiles(VirtualFile[] files)
    {
        Map result = new HashMap();
        for (VirtualFile file : files)
        {
            if ((file.getChildren() != null) && (file.getChildren().length != 0))
            {
                Map mapNew = getVirtualFiles(file.getChildren());
                Set<Map.Entry<String, Set<VirtualFile>>> entrySet = mapNew.entrySet();
                Iterator<Map.Entry<String, Set<VirtualFile>>> iterator = entrySet.iterator();
                while (iterator.hasNext())
                {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    if (result.containsKey(entry.getKey()))
                    {
                        ((Set)result.get(entry.getKey())).addAll((Collection)entry.getValue());
                    }
                    else
                    {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            else {
                if (file.isDirectory())
                    continue;
                String extension = file.getExtension();
                if ((extension == null) || (extension.trim().length() <= 0))
                    continue;
                extension = extension.toLowerCase();
                if (!result.containsKey(extension))
                {
                    result.put(extension, new HashSet());
                }
                Set virtualFiles = (Set)result.get(extension);
                virtualFiles.add(file);
            }
        }
        return result;
    }

    // Get line count of file
    public static int getLineCount(VirtualFile file)
    {
        int lineCount = 0;
        try
        {
            lineCount = getLineCount(file.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return lineCount;
    }

    // Get code line count of file
    public static int getCodeLineCount(VirtualFile file)
    {
        int codeLineCount = 0;
        try
        {
            codeLineCount = getCodeLineCount(file.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return codeLineCount;
    }

    // Get method count of file
    public static int getCalledCodeLineCount(VirtualFile file)
    {
        int codeLineCount = 0;
        try
        {
            codeLineCount = getCalledCodeLineCount(file.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return codeLineCount;
    }

    // Get html code line count of file
    public static int getHtmlCodeLineCount(VirtualFile file)
    {
        int codeLineCount = 0;
        try
        {
            codeLineCount = getHtmlCodeLineCount(file.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return codeLineCount;
    }

    // Buffer through file to count the total lines
    private static int getLineCount(InputStream stream)
    {
        int lineCount = 0;
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(stream);
            LineNumberReader streamReader = new LineNumberReader(new InputStreamReader(inputStream));
            while (streamReader.ready())
            {
                streamReader.readLine();
                lineCount++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return lineCount;
    }

    // Buffer through file to only count the source code lines
    // Commented lines are ignored
    private static int getCodeLineCount(InputStream stream)
    {
        boolean isInComment = false;
        Integer total = Integer.valueOf(0);
        Integer blank = Integer.valueOf(0);
        Integer comment = Integer.valueOf(0);
        Integer code = Integer.valueOf(0);
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(stream);
            LineNumberReader streamReader = new LineNumberReader(new InputStreamReader(inputStream));
            Integer localInteger1;
            while (streamReader.ready())
            {
                String line = streamReader.readLine();
                if (line != null)
                {
                    localInteger1 = total; Integer localInteger2 = total = Integer.valueOf(total.intValue() + 1);
                    line = line.trim();
                    if (line.length() == 0)
                    {
                        localInteger1 = blank; localInteger2 = blank = Integer.valueOf(blank.intValue() + 1);
                        continue;
                    }
                    if (line.startsWith("//"))
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        continue;
                    }
                    if (isInComment)
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        if (!line.contains("*/"))
                            continue;
                        isInComment = false; continue;
                    }
                    if (line.startsWith("/*"))
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        if (line.contains("*/"))
                            continue;
                        isInComment = true; continue;
                    }

                    localInteger1 = code; localInteger2 = code = Integer.valueOf(code.intValue() + 1);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return code;
    }

    // Buffer through file to count methods in java
    private static int getCalledCodeLineCount(InputStream stream)
    {
        boolean isInComment = false;
        boolean isInMethod = false;
        Integer total = Integer.valueOf(0);
        Integer blank = Integer.valueOf(0);
        Integer comment = Integer.valueOf(0);
        Integer code = Integer.valueOf(0);
        Integer calledMethodcode = Integer.valueOf(0);
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(stream);
            LineNumberReader streamReader = new LineNumberReader(new InputStreamReader(inputStream));
            Integer localInteger1;
            while (streamReader.ready())
            {
                String line = streamReader.readLine();
                if (line != null)
                {
                    localInteger1 = total; Integer localInteger2 = total = Integer.valueOf(total.intValue() + 1);
                    line = line.trim();
                    if (line.length() == 0)
                    {
                        localInteger1 = blank; localInteger2 = blank = Integer.valueOf(blank.intValue() + 1);
                        continue;
                    }
                    if ((line.startsWith("private")|| line.startsWith("public") || line.startsWith("protected")) && !line.contains(";"))
                    {
                        localInteger1 = calledMethodcode; localInteger2 = calledMethodcode = Integer.valueOf(calledMethodcode.intValue() + 1);
                        continue;
                    }
                    if (isInComment)
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        if (!line.contains("*/"))
                            continue;
                        isInComment = false; continue;
                    }

                    if (line.startsWith("/*"))
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        if (line.contains("*/"))
                            continue;
                        isInComment = true; continue;
                    }
                    localInteger1 = code; localInteger2 = code = Integer.valueOf(code.intValue() + 1);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return calledMethodcode;
    }

    // Buffer through file to count code lines in html
    private static int getHtmlCodeLineCount(InputStream stream)
    {
        boolean isInComment = false;
        Integer total = Integer.valueOf(0);
        Integer blank = Integer.valueOf(0);
        Integer comment = Integer.valueOf(0);
        Integer code = Integer.valueOf(0);
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(stream);
            LineNumberReader streamReader = new LineNumberReader(new InputStreamReader(inputStream));
            Integer localInteger1;
            while (streamReader.ready())
            {
                String line = streamReader.readLine();
                if (line != null)
                {
                    localInteger1 = total; Integer localInteger2 = total = Integer.valueOf(total.intValue() + 1);
                    line = line.trim();
                    if (line.length() == 0)
                    {
                        localInteger1 = blank; localInteger2 = blank = Integer.valueOf(blank.intValue() + 1);
                        continue;
                    }
                    if (line.startsWith("//"))
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        continue;
                    }
                    if (isInComment)
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        if (!line.contains("-->"))
                            continue;
                        isInComment = false; continue;
                    }

                    if (line.startsWith("<!--"))
                    {
                        localInteger1 = comment; localInteger2 = comment = Integer.valueOf(comment.intValue() + 1);
                        if (line.contains("-->"))
                            continue;
                        isInComment = true; continue;
                    }
                    localInteger1 = code; localInteger2 = code = Integer.valueOf(code.intValue() + 1);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return code;
    }
}