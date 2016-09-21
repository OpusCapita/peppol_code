package com.opuscapita.peppol.support.ui.dao;

import org.hibernate.HibernateException;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.PropertyAccessorFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.14.12
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
public class AliasToBeanNestedResultTransformer extends AliasedTupleSubsetResultTransformer {

    private static final long serialVersionUID = -8047276133980128266L;

    private final Class<?> resultClass;

    public AliasToBeanNestedResultTransformer(Class<?> resultClass) {

        this.resultClass = resultClass;
    }

    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    private List<Object> createSubclassList(String fieldName, Class<?> subclassCollection) {
        List<Object> list = new ArrayList<Object>();
        list.add(new ArrayList<Object>());
        list.add(new ArrayList<String>());
        list.add(fieldName);
        if (subclassCollection != null) {
            list.add(subclassCollection);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public Object transformTuple(Object[] tuple, String[] aliases) {

        Map<Class<?>, List<?>> subclassToAlias = new HashMap<Class<?>, List<?>>();
        // Map<Class<?>, List<?>> nestedSubclassToAlias = new HashMap<Class<?>, List<?>>();
        List<String> nestedAliases = new ArrayList<String>();

        try {
            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                if (alias.contains(".")) {
                    String fieldName = "";
                    String aliasName = "";
                    Class<?> subclassCollection = null;
                    Class<?> subclass = null;
                    nestedAliases.add(alias);
                    Class<?> lastField = resultClass;
                    String[] sp = alias.split("\\.");
                    for (int j = 0; j < sp.length - 1; j++) {
                        fieldName = sp[j];
                        Field field = lastField.getDeclaredField(fieldName);
                        subclass = field.getType();
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) genericType;
                            for (Type t : pt.getActualTypeArguments()) {
                                subclassCollection = subclass;
                                subclass = ((Class<?>) t);
                            }
                        }
                        if (sp.length > 2 && subclassToAlias.containsKey(subclass)) {
                            lastField = subclass;
                        }
                    }
                    if (sp.length > 2 && lastField == resultClass) {
                        throw new NoSuchFieldException();
                    }
                    aliasName = sp[sp.length - 1];

                    if (lastField != resultClass) {
                        List<Object> lastFieldList = (List<Object>) subclassToAlias.get(lastField);
                        Map<Class<?>, List<?>> nestedSubclassToAlias;
                        if (lastFieldList.size() > 4) {
                            nestedSubclassToAlias = (HashMap<Class<?>, List<?>>) lastFieldList.get(4);
                        } else {
                            nestedSubclassToAlias = new HashMap<Class<?>, List<?>>();
                            lastFieldList.add(nestedSubclassToAlias);
                        }
                        if (!nestedSubclassToAlias.containsKey(subclass)) {
                            List<Object> list = createSubclassList(fieldName, subclassCollection);
                            nestedSubclassToAlias.put(subclass, list);
                        }
                        ((List<Object>) nestedSubclassToAlias.get(subclass).get(0)).add(tuple[i]);
                        ((List<String>) nestedSubclassToAlias.get(subclass).get(1)).add(aliasName);
                    } else {
                        if (!subclassToAlias.containsKey(subclass)) {
                            List<Object> list = createSubclassList(fieldName, subclassCollection);
                            subclassToAlias.put(subclass, list);
                        }
                        ((List<Object>) subclassToAlias.get(subclass).get(0)).add(tuple[i]);
                        ((List<String>) subclassToAlias.get(subclass).get(1)).add(aliasName);
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
        }

        Object[] newTuple = new Object[aliases.length - nestedAliases.size()];
        String[] newAliases = new String[aliases.length - nestedAliases.size()];
        int i = 0;
        for (int j = 0; j < aliases.length; j++) {
            if (!nestedAliases.contains(aliases[j])) {
                newTuple[i] = tuple[j];
                newAliases[i] = aliases[j];
                ++i;
            }
        }

        ResultTransformer rootTransformer = new AliasToBeanResultTransformer(resultClass);
        Object root = rootTransformer.transformTuple(newTuple, newAliases);

        for (Class<?> subclass : subclassToAlias.keySet()) {
            ResultTransformer subclassTransformer = new AliasToBeanResultTransformer(subclass);
            Object subObject = subclassTransformer.transformTuple(
                    ((List<Object>) subclassToAlias.get(subclass).get(0)).toArray(),
                    ((List<Object>) subclassToAlias.get(subclass).get(1)).toArray(new String[0])
            );

            PropertyAccessor accessor = PropertyAccessorFactory.getPropertyAccessor("property");
            if (subclassToAlias.get(subclass).size() > 3) {
                Object obj = subclassToAlias.get(subclass).get(3);
                Collection<Object> collection = null;
                if (obj.getClass().isInstance(Set.class)) {
                    collection = new HashSet<Object>();
                } else if (obj.getClass().isInstance(List.class) || obj.getClass().isInstance(Queue.class)) {
                    collection = new LinkedList<Object>();
                }
                collection.add(subObject);
                accessor.getSetter(resultClass, (String) subclassToAlias.get(subclass).get(2)).set(root, collection, null);

            } else {
                accessor.getSetter(resultClass, (String) subclassToAlias.get(subclass).get(2)).set(root, subObject, null);
            }

        }

        return root;
    }
}
