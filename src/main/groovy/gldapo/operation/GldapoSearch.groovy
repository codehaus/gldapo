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
package gldapo.operation
import gldapo.Gldapo
import gldapo.GldapoSearchProvider
import gldapo.GldapoSearchControls
import gldapo.schema.annotation.GldapoSchemaFilter

/**
 * Represents an actual search operation. Sanitises the search options then calls {@link GldapoDirectory#search}.
 */
class GldapoSearch extends AbstractGldapoOptionSubjectableOperation
{    
    
    GldapoSearch() {
        super()
        required = ["schema"]
        optionals = ["directory", "filter", "base", "absoluteBase", "countLimit", "derefLinkFlag", "searchScope", "timeLimit"]
    }
    
    void inspectOptions() {
        this.options.directory = this.calculateDirectory()
        this.options.filter = this.calculateFilter()
        this.options.searchControls = this.calculateSearchControls()
        this.options.base = this.calculateBase()
    }
    
    def calculateDirectory()
    {
        if (options.directory != null)
        {
            def directoryValue = options.directory
            if (directoryValue instanceof String) return this.gldapo.directories[directoryValue]
            
            if (directoryValue instanceof GldapoSearchProvider) return directoryValue

            // TODO more suitable exception needed
            throw new IllegalArgumentException()
        }
        else
        {
            return this.gldapo.directories.defaultDirectory
        }        
    }
    
    def calculateFilter()
    {
        def schemaFilter = this.options.schema.getAnnotation(GldapoSchemaFilter)?.value()
        
        if (this.options.filter) return (schemaFilter) ? "(&${schemaFilter}${this.options.filter})" : this.options.filter
        else return (schemaFilter) ? schemaFilter : "(objectclass=*)"
    }
    
    def calculateSearchControls()
    {
        def specificControls = new GldapoSearchControls(this.options)
        (this.options.directory.searchControls) ? this.options.directory.searchControls.mergeWith(specificControls) : specificControls
    }
    
    def calculateBase()
    {
        if (options.containsKey("absoluteBase")) return options.absoluteBase - ",${this.options.directory.base}"
        else if (options.containsKey("base")) return options.base
        else return ""
    }
    
    def execute()
    {
        this.options.directory.search(this.gldapo.schemas[this.options.schema], this.options.base, this.options.filter, this.options.searchControls)
    }
}
