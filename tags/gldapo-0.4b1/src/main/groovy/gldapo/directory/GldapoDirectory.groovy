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
package gldapo.directory

import gldapo.Gldapo
import gldapo.schema.GldapoContextMapper
import gldapo.exception.GldapoException
import gldapo.exception.GldapoInvalidConfigException
import gldapo.exception.GldapoInvalidConfigException
import gldapo.util.FilterUtil
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import org.springframework.ldap.filter.Filter
import org.springframework.ldap.core.LdapOperations
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler
import org.springframework.ldap.core.ContextMapperCallbackHandler
import org.springframework.ldap.control.PagedResultsRequestControl
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.LimitExceededException
import org.springframework.ldap.core.ContextMapper
import org.springframework.beans.factory.BeanNameAware
import javax.naming.directory.SearchControls

class GldapoDirectory implements BeanNameAware, GldapoSearchProvider
{
	static final CONFIG_SEARCH_CONTROLS_KEY = 'searchControls'
	
	/**
	 * 
	 */
	GldapoSearchControlProvider searchControls
	
	/**
	 * 
	 */
	String name
	
	/**
	 * 
	 */
	def template
	
	/**
	 * The stub generator doesn't generate getter/setters properly. Have to put them in manually for now.
	 */
	void setBeanName(String beanName) {
		this.name = beanName
	}
	
	GldapoSearchControlProvider getSearchControls() {
		this.searchControls
	}
	
	/**
	 * Simply retrieves the property from the context source
	 */
	String getBase()
	{
		template?.contextSource?.base as String
	}
	
	List search(Class schema, String base, String filter, GldapoSearchControlProvider controls, Integer pageSize)
	{
		def schemaRegistration = Gldapo.instance.schemas[schema]
		
		ContextMapper mapper = new GldapoContextMapper(schemaRegistration: schemaRegistration, directory: this)
		ContextMapperCallbackHandler handler = new ContextMapperCallbackHandler(mapper)

		SearchControls jndiControls = controls as SearchControls
		jndiControls.returningAttributes = schemaRegistration.attributeMappings*.attributeName
		
		try
		{
			PagedResultsRequestControl requestControl = new PagedResultsRequestControl(pageSize)
			this.template.search(base, filter, jndiControls, handler, requestControl)
		
			while (requestControl?.cookie?.cookie != null)
			{
				requestControl = new PagedResultsRequestControl(pageSize, requestControl.cookie)
				this.template.search(base, filter, jndiControls, handler, requestControl)
			} 

			return handler.list
		}
		catch (LimitExceededException e)
		{
			// If the number have entries has hit the specified count limit OR
			// The server is unwilling to send more entries we will get here.
			// It's not really an error condition hence we just return what we found.
		
			return handler.list
		}
	}
	
	/**
	 * @todo Implement tighter checking that the config parameters are valid, for spelling mistakes and such
	 */
	static newInstance(String name, Map config)
	{
	    if (config == null) throw new GldapoInvalidConfigException("Config for directory '$name' is null" as String)
	    
		def contextSource = GldapoContextSource.newInstance(config)
		
		def template = new LdapTemplate(contextSource: contextSource)
		template.afterPropertiesSet()
		
		def searchControls = GldapoSearchControls.newInstance(config[CONFIG_SEARCH_CONTROLS_KEY])
		
		def directory = new GldapoDirectory(name: name, template: template, searchControls: searchControls)

		return directory
	}
}