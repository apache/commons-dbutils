package org.apache.commons.dbutils2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;


public class EntityUtils {

    // static methods only
    private EntityUtils() {
    }

    /**
     * Given an entity, returns the table name for the entity.
     * @param entity the entity to lookup.
     * @return the name of the table for the entity.
     */
    public static String getTableName(final Class<?> entity) {
        final Entity annotation = entity.getAnnotation(Entity.class);

        if(annotation == null) {
            throw new IllegalArgumentException(entity.getName() + " does not have the Entity annotation");
        }

        final Table table = entity.getAnnotation(Table.class);

        // get the table's name from the annotation
        if(table != null && !table.name().isEmpty()) {
            return table.name();
        } else {
            return entity.getSimpleName();
        }
    }

    /**
     * Given an entity, gets the @Id of the entity, assuming only one ID column.
     * @param entityClass the type of the entity.
     * @param entity the instance of the entity.
     * @return the value of the ID.
     */
    @SuppressWarnings("unchecked")
    public static <T, I> I getId(final Class<T> entityClass, T entity) {
        Map<String, String> idColumns = getIdColumns(entityClass);

        if(idColumns.size() != 1) {
            throw new IllegalArgumentException("Cannot get ID for this entity, wrong number of IDs: " + idColumns.size());
        }

        try {
            final String column = idColumns.keySet().toArray(new String[0])[0];
            return (I) PropertyUtils.getSimpleProperty(entity, idColumns.get(column));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gets the names of the columns for a given entity, except those marked as @GeneratedValue.
     * @param entity the entity to search.
     * @return a map which contains column name, and field name.
     */
    static Map<String, String> getColumns(Class<?> entityClass) {
        return getColumns(entityClass, false);
    }

    /**
     * Gets the names of the columns for a given entity, except those marked as @GeneratedValue.
     * @param entity the entity to search.
     * @param excludeIds excludes columns marked with @Id when true
     * @return a map which contains column name, and field name.
     */
    static Map<String, String> getColumns(Class<?> entityClass, boolean excludeIds) {
        final Map<String, String> ret = new HashMap<String, String>();

        if(entityClass.getAnnotation(Entity.class) == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        // we need to walk up the inheritance chain
        while(entityClass != null) {
            for(Field field:entityClass.getDeclaredFields()) {
                final Column column = field.getAnnotation(Column.class);
                final Id id = field.getAnnotation(Id.class);
                final GeneratedValue gen = field.getAnnotation(GeneratedValue.class);

                // if we only want IDs, and this isn't an @Id, then skip
                if(excludeIds && id != null) {
                    continue;
                // if we want all columns, then must be marked as a column and not auto-generated
                } else if(column == null || gen != null) {
                    continue;
                }

                String columnName;

                // get the column name or field name
                if(column.name().isEmpty()) {
                    columnName = field.getName();
                } else {
                    columnName = column.name();
                }

                if(ret.put(columnName, field.getName()) != null) {
                    throw new IllegalArgumentException("Entity contains two columns with the same name: " + columnName);
                }
            }

            // walk up the inheritance class
            entityClass = entityClass.getSuperclass();
        }

        if(ret.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        return ret;
    }

    /**
     * Gets the names of the columns that are marked with @Id.
     * @param entity the entity to search.
     * @return a map which contains column name, and field name.
     */
    public static Map<String, String> getIdColumns(Class<?> entityClass) {
        final Map<String, String> ret = new HashMap<String, String>();

        if(entityClass.getAnnotation(Entity.class) == null) {
            throw new IllegalArgumentException(entityClass.getName() + " does not have the Entity annotation");
        }

        // we need to walk up the inheritance chain
        while(entityClass != null) {
            for(Field field:entityClass.getDeclaredFields()) {
                final Column column = field.getAnnotation(Column.class);
                final Id id = field.getAnnotation(Id.class);

                // if we only want IDs, and this isn't an @Id, then skip
                if(column == null || id == null) {
                    continue;
                }

                String columnName;

                // get the column name or field name
                if(column.name().isEmpty()) {
                    columnName = field.getName();
                } else {
                    columnName = column.name();
                }

                if(ret.put(columnName, field.getName()) != null) {
                    throw new IllegalArgumentException("Entity contains two columns with the same name: " + columnName);
                }
            }

            // walk up the inheritance class
            entityClass = entityClass.getSuperclass();
        }

        if(ret.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        return ret;
    }

    /**
     * Takes a set of strings (columns) and joins them with commas and a possible prefix.
     * @param columns the set of columns.
     * @param prefix a prefix.
     * @return the joined columns.
     */
    static String joinColumnsWithComma(final Set<String> columns, final String prefix) {
        final StringBuilder sb = new StringBuilder();

        if(columns.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        final Iterator<String> it = columns.iterator();

        if(prefix != null) {
            sb.append(prefix);
        }

        sb.append(it.next());

        while(it.hasNext()) {
            sb.append(",");

            if(prefix != null) {
                sb.append(prefix);
            }

            sb.append(it.next());
        }

        return sb.toString();
    }

    /**
     * Takes a set of strings (columns) and joins them with equals and a delimiter.
     * @param columns the columns to join.
     * @param delimiter the delimiter between the pairs.
     * @return the joined columns.
     */
    static String joinColumnsEquals(final Set<String> columns, final String delimiter) {
        final StringBuilder sb = new StringBuilder();

        if(columns.isEmpty()) {
            throw new IllegalArgumentException("Entity does not contain any columns");
        }

        final Iterator<String> it = columns.iterator();

        String column = it.next();

        sb.append(column);
        sb.append(" = :");
        sb.append(column);

        while(it.hasNext()) {
            sb.append(delimiter);

            column = it.next();

            sb.append(column);
            sb.append(" = :");
            sb.append(column);
        }

        return sb.toString();
    }

}
