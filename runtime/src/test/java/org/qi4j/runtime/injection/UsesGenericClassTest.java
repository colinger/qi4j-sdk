/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
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
package org.qi4j.runtime.injection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.qi4j.api.composite.TransientBuilder;
import org.qi4j.api.composite.TransientComposite;
import org.qi4j.api.injection.scope.Uses;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;

/**
 * Test of generic class injection
 */
public class UsesGenericClassTest
    extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.addTransients( TestCase.class );
    }

    @Test
    public void givenMixinUsesGenericClassWhenUseClassThenInjectWorks()
    {
        TransientBuilder<TestCase> builder = transientBuilderFactory.newTransientBuilder( TestCase.class );

        builder.use( UsesGenericClassTest.class );

        TestCase testCase = builder.newInstance();
        assertThat( "class name is returned", testCase.test(), equalTo( UsesGenericClassTest.class.getName() ) );
    }

    @Mixins( TestMixin.class )
    public interface TestCase
        extends TransientComposite
    {
        String test();
    }

    public abstract static class TestMixin
        implements TestCase
    {
        @Uses
        Class<? extends TestCase> clazz;

        public String test()
        {
            return clazz.getName();
        }
    }
}