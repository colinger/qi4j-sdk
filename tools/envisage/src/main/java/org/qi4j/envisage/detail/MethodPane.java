/*  Copyright 2009 Tonny Kohar.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.envisage.detail;

import org.qi4j.api.util.Classes;
import org.qi4j.envisage.model.descriptor.*;
import org.qi4j.envisage.model.util.DescriptorUtilities;
import org.qi4j.envisage.util.TableRow;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Implementation of Composite Method Panel
 */
public class MethodPane
    extends DetailPane
{
    protected ResourceBundle bundle = ResourceBundle.getBundle( this.getClass().getName() );

    private JPanel contentPane;
    private JList methodList;
    private JTable methodDetailTable;
    private JSplitPane splitPane;

    private DefaultListModel methodListModel;
    private MethodDetailTableModel methodDetailTableModel;

    public MethodPane( DetailModelPane detailModelPane )
    {
        super( detailModelPane );
        this.setLayout( new BorderLayout() );
        this.add( contentPane, BorderLayout.CENTER );

        methodListModel = new DefaultListModel();
        methodList.setModel( methodListModel );
        methodList.setCellRenderer( new MethodListCellRenderer() );
        methodList.setPrototypeCellValue( "12345678901234567890" );
        methodList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        methodDetailTableModel = new MethodDetailTableModel();
        methodDetailTable.setModel( methodDetailTableModel );

        TableColumnModel columnModel = methodDetailTable.getColumnModel();
        columnModel.getColumn( 0 ).setPreferredWidth( 75 );
        columnModel.getColumn( 1 ).setPreferredWidth( 400 );

        //splitPane.setResizeWeight( .1 );
        //splitPane.setDividerLocation( .3 );

        methodList.addListSelectionListener( new ListSelectionListener()
        {
            public void valueChanged( ListSelectionEvent evt )
            {
                methodListValueChanged( evt );
            }
        } );
    }

    public void setDescriptor( Object objectDesciptor )
    {
        clear();

        if( objectDesciptor instanceof CompositeDetailDescriptor )
        {
            CompositeDetailDescriptor descriptor = ( (CompositeDetailDescriptor) objectDesciptor );
            List<CompositeMethodDetailDescriptor> list = DescriptorUtilities.findMethod( descriptor );
            for( CompositeMethodDetailDescriptor methodDescriptor : list )
            {
                methodListModel.addElement( methodDescriptor );
            }

            if( !methodListModel.isEmpty() )
            {
                methodList.setSelectedIndex( 0 );
            }
        }
        else if( objectDesciptor instanceof ObjectDetailDescriptor )
        {
            // Object does not have methods
            return;
        }
    }

    protected void clear()
    {
        methodListModel.clear();
        methodDetailTableModel.clear();
    }

    private void methodListValueChanged( ListSelectionEvent evt )
    {
        if( evt.getValueIsAdjusting() )
        {
            return;
        }
        Object obj = methodList.getSelectedValue();
        if( obj == null )
        {
            methodDetailTableModel.clear();
            return;
        }
        methodDetailTableModel.reload( (CompositeMethodDetailDescriptor) obj );
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        contentPane = new JPanel();
        contentPane.setLayout( new BorderLayout( 0, 0 ) );
        splitPane = new JSplitPane();
        contentPane.add( splitPane, BorderLayout.CENTER );
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane.setLeftComponent( scrollPane1 );
        methodList = new JList();
        scrollPane1.setViewportView( methodList );
        final JScrollPane scrollPane2 = new JScrollPane();
        splitPane.setRightComponent( scrollPane2 );
        methodDetailTable = new JTable();
        scrollPane2.setViewportView( methodDetailTable );
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return contentPane;
    }

    class MethodDetailTableModel
        extends AbstractTableModel
    {
        /**
         * the column names for this model
         */
        //protected String[] columnNames = { bundle.getString( "Name.Column" ), bundle.getString( "Value.Column" ) };
        protected String[] columnNames = { "Name", "Value" };
        protected ArrayList<TableRow> rows;

        public MethodDetailTableModel()
        {
            rows = new ArrayList<TableRow>();
        }

        public void reload( CompositeMethodDetailDescriptor descriptor )
        {
           Method method = descriptor.descriptor().method();

            clear();

            // mixin type
           rows.add( new TableRow( 2, new Object[]{
                "return", Classes.getSimpleGenericName( method.getGenericReturnType() )
            } ) );

           // method
           StringBuilder parameters = new StringBuilder();
           for (int idx = 0; idx < method.getGenericParameterTypes().length; idx++)
           {
              Type type = method.getGenericParameterTypes()[idx];
              Annotation[] annotations = method.getParameterAnnotations()[idx];

              if (parameters.length() > 0)
                 parameters.append(", ");

              for (Annotation annotation : annotations)
              {
                 String ann = annotation.toString();
                 ann = "@"+ann.substring( ann.lastIndexOf('.' )+1);
                 parameters.append( ann ).append( " " );
              }

              parameters.append( Classes.getSimpleGenericName( type ));
           }

           rows.add( new TableRow( 2, new Object[]{ "parameters", parameters.toString() } ) );

            // concern
            boolean first = true;
            for( MethodConcernDetailDescriptor concern : descriptor.concerns().concerns() )
            {
                if( first )
                {
                    rows.add( new TableRow( 2, new Object[]{ "concern", concern.descriptor().modifierClass().getSimpleName() } ) );
                    first = false;
                }
                else
                {
                    rows.add( new TableRow( 2, new Object[]{ "", concern.descriptor().modifierClass().getSimpleName() } ) );
                }
            }

            // sideEffect
            first = false;
            for( MethodSideEffectDetailDescriptor sideEffect : descriptor.sideEffects().sideEffects() )
            {
                if( first )
                {
                    rows.add( new TableRow( 2, new Object[]{ "sideEffect", sideEffect.descriptor().modifierClass().getSimpleName() } ) );
                    first = false;
                }
                else
                {
                    rows.add( new TableRow( 2, new Object[]{ "", sideEffect.descriptor().modifierClass().getSimpleName() } ) );
                }
            }

            fireTableDataChanged();
        }

        public Object getValueAt( int rowIndex, int columnIndex )
        {
            TableRow row = this.rows.get( rowIndex );
            return row.get( columnIndex );
        }

        public void clear()
        {
            rows.clear();
            fireTableDataChanged();
        }

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public String getColumnName( int col )
        {
            return columnNames[ col ];
        }

        public int getRowCount()
        {
            return rows.size();
        }
    }

    class MethodListCellRenderer
        extends DefaultListCellRenderer
    {
        protected Icon publicIcon;
        protected Icon privateIcon;

        public MethodListCellRenderer()
        {
            try
            {
                publicIcon = new ImageIcon( getClass().getResource( bundle.getString( "ICON_Public" ) ) );
                privateIcon = new ImageIcon( getClass().getResource( bundle.getString( "ICON_Private" ) ) );
            }
            catch( Exception ex )
            {
                throw new RuntimeException( ex );
            }
        }

        public Component getListCellRendererComponent( JList list,
                                                       Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus
        )
        {
            super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            if( !( value instanceof CompositeMethodDetailDescriptor ) )
            {
                return this;
            }

            Icon icon = null;
            CompositeMethodDetailDescriptor descriptor = (CompositeMethodDetailDescriptor) value;

            Class compositeClass = descriptor.composite().descriptor().type();
            Class mixinMethodClass = descriptor.descriptor().method().getDeclaringClass();
            if( mixinMethodClass.isAssignableFrom( compositeClass ) )
            {
                icon = publicIcon;
            }
            else
            {
                icon = privateIcon;
            }

            if( icon != null )
            {
                setIcon( icon );
            }

            return this;
        }
    }
}
