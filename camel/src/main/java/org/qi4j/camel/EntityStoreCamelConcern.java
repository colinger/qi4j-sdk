/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.camel;

import org.qi4j.composite.ConcernOf;
import org.qi4j.spi.entity.ConcurrentEntityStateModificationException;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.EntityStore;
import org.qi4j.spi.entity.EntityStoreException;
import org.qi4j.spi.entity.QualifiedIdentity;
import org.qi4j.spi.entity.StateCommitter;

/**
 * TODO
 */
public abstract class EntityStoreCamelConcern
    extends ConcernOf<EntityStore>
    implements EntityStore
{
    public StateCommitter prepare( Iterable<EntityState> newStates, Iterable<EntityState> loadedStates, Iterable<QualifiedIdentity> removedStates ) throws EntityStoreException, ConcurrentEntityStateModificationException
    {
        final StateCommitter committer = next.prepare( newStates, loadedStates, removedStates );
        return new StateCommitter()
        {
            public void commit()
            {
                committer.commit();

                // Send to camel
            }

            public void cancel()
            {
                committer.cancel();
            }
        };
    }
}
