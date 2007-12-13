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
package gldapo
import gldapo.schema.attribute.type.DefaultTypeMappings
import gldapo.schema.attribute.AbstractAttributeMapping

class GldapoTypeMappingRegistry extends LinkedList<Class>
{
	GldapoTypeMappingRegistry()
	{
		super()
		this << DefaultTypeMappings
	}
	
	def getToFieldMapperForType(String type)
	{
		findMapper(AbstractAttributeMapping.toFieldByTypeMapperName(type), Object)
	}
	
	def findMapper(String mapperName, Class[] argTypes)
	{
		def mapping
		def provider = this.reverse().find {
			mapping = it.metaClass.getMetaMethod(mapperName, argTypes)
			return mapping?.isStatic()
		}
		
		(provider && mapping) ? { mapping.invoke(provider, it) } : null
	}
}