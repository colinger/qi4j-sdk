/*
 * Copyright 2006 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.qi4j.runtime.injection.provider;

import java.lang.reflect.Type;
import org.qi4j.Qi4j;
import org.qi4j.composite.CompositeBuilderFactory;
import org.qi4j.entity.UnitOfWorkFactory;
import org.qi4j.object.ObjectBuilderFactory;
import org.qi4j.runtime.Qi4jRuntime;
import org.qi4j.runtime.composite.Resolution;
import org.qi4j.runtime.injection.DependencyModel;
import org.qi4j.runtime.injection.InjectionContext;
import org.qi4j.runtime.injection.InjectionProvider;
import org.qi4j.runtime.injection.InjectionProviderFactory;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.service.ServiceFinder;
import org.qi4j.spi.Qi4jSPI;
import org.qi4j.structure.Module;

public final class StructureInjectionProviderFactory
    implements InjectionProviderFactory
{
    public InjectionProvider newInjectionProvider( Resolution resolution, DependencyModel dependencyModel ) throws InvalidInjectionException
    {
        return new StructureInjectionProvider( resolution, dependencyModel );
    }

    private static class StructureInjectionProvider
        implements InjectionProvider
    {
        final Resolution resolution;
        private final DependencyModel dependencyModel;

        private StructureInjectionProvider( Resolution resolution, DependencyModel dependencyModel )
        {
            this.resolution = resolution;
            this.dependencyModel = dependencyModel;
        }

        public Object provideInjection( InjectionContext context ) throws InjectionProviderException
        {
            Type type = dependencyModel.injectionType();

            if( type.equals( CompositeBuilderFactory.class ) )
            {
                return context.moduleInstance().compositeBuilderFactory();
            }
            else if( type.equals( ObjectBuilderFactory.class ) )
            {
                return context.moduleInstance().objectBuilderFactory();
            }
            else if( type.equals( UnitOfWorkFactory.class ) )
            {
                return context.moduleInstance().unitOfWorkFactory();
            }
            else if( type.equals( ServiceFinder.class ) )
            {
                return context.moduleInstance().serviceFinder();
            }
            else if( type.equals( Module.class ) )
            {
                return context.moduleInstance();
            }
            else if( type.equals( Qi4j.class ) || type.equals( Qi4jSPI.class ) || type.equals( Qi4jRuntime.class ) )
            {
                return ( (ModuleInstance) context.moduleInstance() ).layerInstance().applicationInstance().runtime();
            }

            return null;
        }
    }
}
