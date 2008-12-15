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
package gldapo.schema.attribute.validator
import java.lang.annotation.Annotation
import gldapo.schema.attribute.AttributeMapping
import gldapo.schema.constraint.InvalidConstraintException

/**
 * Validates a value, given an attribute mapping and a constraint instance.
 */
interface AttributeValidator {
    
    /**
     * Will be called with constraint annotation instance declared on the attribute
     */
    void setConstraint(Annotation constraint)
    
    /**
     * Will be called with the underlying attribute mapping for the field
     */
    void setAttributeMapping(AttributeMapping field)
        
    /**
     * If the constraint values are invalid or the type of field is invalid, a 
     * {@link InvalidConstraintException} should be thrown.
     * 
     * The implementation in this class does nothing.
     */
    void init() throws InvalidConstraintException {}
    
    /**
     * Will be called with a value to validate.
     * 
     * If the value is valid, {@code null} MUST be returned. If the value is invalid, either return a 
     * single string error code or a list of string error codes.
     * 
     * @return {@code null} if {@code obj} is valid, otherwise 1 or more (as a list) error codes.
     */
    def validate(obj)
}
