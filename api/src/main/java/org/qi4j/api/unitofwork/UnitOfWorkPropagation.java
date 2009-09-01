/*
 * Copyright (c) 2008, Edward Yakop. All Rights Reserved.
 * Copyright (c) 2009, Niclas Hedhman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.qi4j.api.unitofwork;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Target;
import static org.qi4j.api.unitofwork.UnitOfWorkPropagation.Propagation.*;

/**
 * Annotation to denote the unit of work propagation.
 * <p/>
 * <p/>
 * Usage example:
 * <pre>
 * <code>
 * <p/>
 * &#64;Concerns( UnitOfWorkConcern.class )
 * public class MyBusinessServiceMixin implements BusinessService
 * {
 *   &#64;Structure UnitOfWorkFactory uowf;
 * <p/>
 *   &#64;UnitOfWorkPropagation
 *   public void myBusinessMethod()
 *   {
 *     // Must invoke current unit of work.
 *     UnitOfWork uow = uowf.currentUnitOfWork();
 * <p/>
 *     // Perform business logic
 *   }
 * }
 * </code>
 * </pre>
 */
@Retention( RUNTIME )
@Target( METHOD )
@Inherited
@Documented
public @interface UnitOfWorkPropagation
{
    Propagation value() default REQUIRED;

    enum Propagation
    {
        /**
         * Default propagation behavior.
         * Behavior: <br>
         * If no current transaction: creates a new transaction <br>
         * If there is a current transaction: use the current transaction.
         */
        REQUIRED,

        /**
         * Behavior: <br>
         * If no current transaction: throw an exception <br>
         * If there is a current transaction: use the current transaction.
         */
        MANDATORY,

        /**
         * Behavior: <br>
         * If no current transaction: creates a new transaction <br>
         * If there is a current transaction: suspend the current transaction and create a new transaction.
         */
        REQUIRES_NEW,

        /**
         * Behavior: <br>
         * If no current transaction: creates a new transaction <br>
         * If there is a current transaction: create a nested transaction on top of the current one.
         */
        REQUIRES_NESTED
    }
}
