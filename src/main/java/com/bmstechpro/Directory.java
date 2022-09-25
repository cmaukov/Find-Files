package com.bmstechpro;
/* directory-tree-visitor
 * @created 09/24/2022
 * @author Konstantin Staykov
 */

import java.util.ArrayList;
import java.util.List;

public class Directory {
    private String path;
    private List<String> files;

    public Directory(String path) {
        this.path = path;
        files = new ArrayList<>();
    }

    public void addFile(String fileName){
        files.add(fileName);
    }

    public String getPath() {
        return path;
    }

    public List<String> getFiles() {
        return files;
    }
}
