/*
 * Copyright (c) 2010, Stanislav Muhametsin. All Rights Reserved.
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
package org.qi4j.test.performance.entitystore.sql;

import org.junit.Ignore;
import org.qi4j.api.common.Visibility;
import org.qi4j.api.structure.Application;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.bootstrap.*;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.entitystore.sql.assembly.PostgreSQLEntityStoreAssembler;
import org.qi4j.entitystore.sql.internal.SQLs;
import org.qi4j.library.sql.common.SQLConfiguration;
import org.qi4j.library.sql.common.SQLUtil;
import org.qi4j.library.sql.ds.PGDataSourceConfiguration;
import org.qi4j.test.performance.entitystore.model.AbstractEntityStorePerformanceTest;

import java.sql.Connection;
import java.sql.Statement;

/**
 * WARN This test is deactivated on purpose, please do not commit it activated.
 * 
 * To run it see {@link PostgreSQLEntityStorePerformanceTest}.
 * 
 * FIXME ES Performance tests seem to be pretty broken at the moment. At least if I want to delete test data after
 * running tests, and also by not waiting for Runnables to finish running.
 * 
 * @author Stanislav Muhametsin
 * @author Paul Merlin
 */
@Ignore
public class PostgreSQLEntityStorePerformanceTest extends AbstractEntityStorePerformanceTest
{

    public PostgreSQLEntityStorePerformanceTest()
    {
        super( "PostgreSQLEntityStore", createAssembler() );
    }

    private static Assembler createAssembler()
    {
        return new Assembler()
        {

            @SuppressWarnings("unchecked")
            public void assemble( ModuleAssembly module )
                throws AssemblyException
            {
                new PostgreSQLEntityStoreAssembler().assemble( module );
                ModuleAssembly configModule = module.layer().module( "config" );
                configModule.services( MemoryEntityStoreService.class );
                configModule.entities( PGDataSourceConfiguration.class, SQLConfiguration.class ).visibleIn(
                    Visibility.layer );
            }

        };
    }

    @Override
    protected void cleanUp()
        throws Exception
    {
        try
        {
            super.cleanUp();
        }
        finally
        {

            Energy4Java qi4j = new Energy4Java();
            Assembler[][][] assemblers = new Assembler[][][]
            {
                {
                    {
                        createAssembler()
                    }
                }
            };
            Application application = qi4j.newApplication( new ApplicationAssemblerAdapter( assemblers )
            {
            } );
            application.activate();

            Module moduleInstance = application.findModule( "Layer 1", "config" );
            UnitOfWorkFactory uowf = moduleInstance;
            UnitOfWork uow = uowf.newUnitOfWork();
            try
            {
                SQLConfiguration config = uow.get( SQLConfiguration.class,
                    PostgreSQLEntityStoreAssembler.ENTITYSTORE_SERVICE_NAME );
                // TODO fix AbstractEntityStorePerformanceTest to extend from AbstractQi4jTest
                Connection connection = null; // SQLUtil.getConnection( this.serviceLocator );
                String schemaName = config.schemaName().get();
                if( schemaName == null )
                {
                    schemaName = SQLs.DEFAULT_SCHEMA_NAME;
                }

                Statement stmt = null;
                try
                {
                    stmt = connection.createStatement();
                    stmt.execute( String.format( "DELETE FROM %s." + SQLs.TABLE_NAME, schemaName ) );
                    connection.commit();
                }
                finally
                {
                    SQLUtil.closeQuietly( stmt );
                }
            }
            finally
            {
                uow.discard();
            }
        }
    }

}