/*
 * Copyright 2007-2008 Niclas Hedhman
 * Copyright 2007-2008 Rickard Öberg
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.qi4j.lang.groovy;

import org.junit.Assert;
import org.junit.Test;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;

public class GroovyMixinTest extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.transients( GroovyComposite.class );
    }

    @Test
    public void testInvoke()
    {
        GroovyComposite domain1 = module.newTransient( GroovyComposite.class );
        GroovyComposite domain2 = module.newTransient( GroovyComposite.class );
        Assert.assertEquals( "do1() in Groovy:1", domain1.do1() );
        Assert.assertEquals( "do1() in Groovy:2", domain1.do1() );
        Assert.assertEquals( "do1() in Groovy:3", domain1.do1() );
        Assert.assertEquals( "do1() in Groovy:4", domain1.do1() );
        Assert.assertEquals( "do1() in Groovy:1", domain2.do1() );
        Assert.assertEquals( "do1() in Groovy:2", domain2.do1() );
        Assert.assertEquals( "do1() in Groovy:3", domain2.do1() );
        Assert.assertEquals( "do1() in Groovy:4", domain2.do1() );
        Assert.assertEquals( "do1() in Groovy:5", domain2.do1() );
        Assert.assertEquals( "do1() in Groovy:6", domain2.do1() );
        Assert.assertEquals( "do1() in Groovy:5", domain1.do1() );
    }
}