/*
 *  Copyright 2011 SPeCS Research Group.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package pt.up.fe.specs.guihelper.Setups.SimpleIo;

import java.io.File;
import java.util.List;

/**
 *
 * @author Joao Bispo
 */
public class SimpleIoData {

   public SimpleIoData(boolean isSingleFile, File inputPath, List<File> inputFiles, File outputFolder) {
      this.isSingleFile = isSingleFile;
      this.inputPath = inputPath;
      this.inputFiles = inputFiles;
      this.outputFolder = outputFolder;
   }

   public final boolean isSingleFile;
   public final File inputPath;
   public final List<File> inputFiles;
   public final File outputFolder;

}
