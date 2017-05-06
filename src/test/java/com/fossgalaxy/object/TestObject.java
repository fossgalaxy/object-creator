package com.fossgalaxy.object;

import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.ObjectDefStatic;
import com.fossgalaxy.object.annotations.Parameter;

/**
 * Created by piers on 05/05/17.
 */
public class TestObject {

    private final String name;
    private UnknownObject unknownObject;

    @ObjectDef("Object")
    public TestObject(String name) {
        this.name = name;
    }

    public TestObject(String name, UnknownObject unknownObject) {
        this.name = name;
        this.unknownObject = unknownObject;
    }

    @ObjectDefStatic("Name")
    public static TestObject makeObject() {
        return new TestObject("Name");
    }

    @ObjectDefStatic("WithUnknown")
    @Parameter(id = 1, func = "makeUnknownObject")
    public static TestObject makeObject(String name, UnknownObject unknownObject) {
        return new TestObject(name, unknownObject);
    }

    @ObjectDefStatic("WithUnknownWrongFunction")
    @Parameter(id = 1, func = "makeUnknownObject2")
    public static TestObject makeObjectWrongFunction(String name, UnknownObject unknownObject) {
        return new TestObject(name, unknownObject);
    }


    @ObjectDefStatic("WithUnknownWrongType")
    @Parameter(id = 1, func = "makeUnknownObjectWrongType")
    public static TestObject makeObjectWrongType(String name, UnknownObject unknownObject) {
        return new TestObject(name, unknownObject);
    }

    public static UnknownObject makeUnknownObject(String info) {
        return new UnknownObject(info);
    }

    public static String makeUnknownObjectWrongType(String info) {
        return info;
    }

    // This is here to make sure we can handle finding the method that has at least an overload of the right signature)
    public static UnknownObject makeUnknownObject(String info, String other){
        return new UnknownObject(info + other);
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "name='" + name + '\'' +
                '}';
    }

    @ObjectDefStatic("WithNonPublicMethod")
    private static TestObject createObjectNonPublic(String name){
        return new TestObject(name);
    }

    @ObjectDefStatic("WithNonStaticMethod")
    public TestObject createObjectNonStatic(String name){
        return new TestObject(name);
    }
}
