/*
* Copyright 2007 the original author or authors.
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
package gldapo.schema.annotation;
import java.lang.annotation.*;

/**
 * Indicates that the attribute requires custom type conversion.
 * 
 * Useful, for example, when dealing with dates. Dates can be stored in different ways,
 * so they need to be parsed differently. To handle this, you could specify the actual type
 * of the attribute as {@link java.util.Date}, but use a psuedo type.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface GldapoPseudoType
{
    String value();
}