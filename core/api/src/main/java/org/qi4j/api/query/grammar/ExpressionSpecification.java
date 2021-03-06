package org.qi4j.api.query.grammar;

import org.qi4j.api.composite.Composite;
import org.qi4j.functional.Specification;

import static org.qi4j.functional.Iterables.*;

/**
 * Base expression Specification.
 */
public abstract class ExpressionSpecification
    implements Specification<Composite>
{

    public AndSpecification and( Specification<Composite> specification )
    {
        if( this instanceof AndSpecification )
        {
            return new AndSpecification( append( specification, ( (AndSpecification) this ).operands() ) );
        }
        else
        {
            return new AndSpecification( iterable( this, specification ) );
        }
    }

    public OrSpecification or( Specification<Composite> specification )
    {
        if( this instanceof OrSpecification )
        {
            return new OrSpecification( append( specification, ( (OrSpecification) this ).operands() ) );
        }
        else
        {
            return new OrSpecification( iterable( this, specification ) );
        }
    }

}
