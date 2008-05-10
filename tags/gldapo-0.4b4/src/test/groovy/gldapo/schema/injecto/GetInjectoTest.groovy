/* 
 * Copyright 2007 Luke Daley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gldapo.schema.injecto
import injecto.*
import gldapo.exception.GldapoNoDefaultDirectoryException
import gldapo.test.GldapoMockOperationInstaller
import gldapo.Gldapo

/**
 * Not much to test here really, it's pretty much the same as find.
 */
class GetInjectoTest extends GroovyTestCase 
{
    static gldapo = new Gldapo()
    
    static {
        use(Injecto) { GetInjectoTestSchema.inject(GetInjecto) }
        GetInjectoTestSchema.gldapo = gldapo
    }

    void testGetWithResult() {
        GldapoMockOperationInstaller.installSearchWithResult([1,2,3], gldapo)
        assertEquals(1, GetInjectoTestSchema.getByDn("abc"))
    }
}

class GetInjectoTestSchema {}