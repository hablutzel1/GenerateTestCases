package com.intellij.generatetestcases.test;

import com.intellij.generatetestcases.model.BDDCore;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 *
 * Utilities for doing some test unit tasks simpler
 * 
 * User: Jaime Hablutzel
 */
public class TestUtil {


    private static PsiDirectory getFirstSourcePackageRoot(Project project) {
        //  create or get source root
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        VirtualFile[] contentSourceRoots = projectRootManager.getContentSourceRoots();
        VirtualFile root = contentSourceRoots[0];
        //  convert this virtualFile to source root (PsiDirectory)
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiDirectory psiDirectory = psiManager.findDirectory(root);
        return psiDirectory;
    }

   public static PsiDirectory createPackageInSourceRoot(String packageName, PsiDirectory sourcePackageRoot) {
        return DirectoryUtil.createSubdirectories(packageName, sourcePackageRoot, ".");
    }

    /**
     * Get plugin base path
     *
     * @return
     */
    public static String getPluginHomePath() {
        final Class aClass = BDDCore.class;
        String rootPath = PathManager.getResourceRoot(aClass, "/" + aClass.getName().replace('.', '/') + ".class");
        assert rootPath != null;
        File root = new File(rootPath).getAbsoluteFile();
        do {
            final String parent = root.getParent();
            if (parent == null) continue;
            root = new File(parent).getAbsoluteFile(); // one step back to get folder
        }
        while (root != null && !(new File(root, FileUtil.toSystemDependentName("META-INF/plugin.xml")).exists()));
        //        String path = new File(s, "plugins/" + "GenerateTestCases").getPath();
        return (root != null) ? root.getAbsolutePath() : null;
//        LocalFileSystem.getInstance().refreshAndFindFileByPath(path.replace(File.separatorChar, '/'));
//        return path;
    }

    public static PsiDirectory createSourceRoot(String sourceRootName, Module module, Collection<File> filesToDelete, PsiManagerImpl myPsiManager) throws IOException {
        File dir = FileUtil.createTempDirectory(sourceRootName, null);
        filesToDelete.add(dir);
        VirtualFile vDir1 = LocalFileSystem.getInstance().refreshAndFindFileByPath(dir.getCanonicalPath().replace(File.separatorChar, '/'));
        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        final ModifiableRootModel rootModel = rootManager.getModifiableModel();
        final ContentEntry contentEntry = rootModel.addContentEntry(vDir1);
        contentEntry.addSourceFolder(vDir1, false);
        rootModel.commit();
        VirtualFile vDir = vDir1;
        //  create it in specified source test root
        PsiDirectory psiDirectory = myPsiManager.findDirectory(vDir);
        return psiDirectory;
    }
}
