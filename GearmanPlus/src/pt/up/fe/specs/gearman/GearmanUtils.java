/**
 * Copyright 2016 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.gearman;

import java.io.IOException;

import org.gearman.Gearman;
import org.gearman.GearmanServer;

import pt.up.fe.specs.util.SpecsLogs;

public class GearmanUtils {

    /**
     * Creates a Gearman server on default port is 4730.
     * 
     * @param gearman
     * @param args
     * @return
     */
    public static GearmanServer newServer(final Gearman gearman, String[] args) {
        return newServer(gearman, 4730, args);
    }

    public static GearmanServer newServer(final Gearman gearman, int port, String[] args) {

        try {
            if (args.length > 0) {
                String addr = args[0];
                SpecsLogs.msgInfo("Connecting to Gearman Server on " + addr + ":" + port);
                return gearman.createGearmanServer(addr, port);
            }

            SpecsLogs.msgInfo("Gearman Server listening on port " + port);
            return gearman.startGearmanServer(port);

        } catch (IOException e) {
            throw new RuntimeException("Exception while trying to start Gearman Server:\n", e);
        }

    }

}
