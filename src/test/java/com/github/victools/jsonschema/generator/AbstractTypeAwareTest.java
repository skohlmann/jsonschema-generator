/*
 * Copyright 2019 VicTools.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.victools.jsonschema.generator;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.github.victools.jsonschema.generator.impl.TypeContextFactory;
import java.util.stream.Stream;
import org.junit.Before;
import org.mockito.Mockito;

/**
 * Abstract test class catering for the type resolution of a dummy/test class to perform tests against introspected fields/methods.
 */
public class AbstractTypeAwareTest {

    private final Class<?> testClass;
    private SchemaGenerationContext context;
    private ResolvedTypeWithMembers testClassMembers;

    protected AbstractTypeAwareTest(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Before
    public final void abstractSetUp() {
        TypeContext typeContext = TypeContextFactory.createDefaultTypeContext();
        ResolvedType resolvedTestClass = typeContext.resolve(this.testClass);
        this.testClassMembers = typeContext.resolveWithMembers(resolvedTestClass);
        this.context = Mockito.mock(SchemaGenerationContext.class);
        Mockito.when(this.context.getTypeContext()).thenReturn(typeContext);
    }

    protected SchemaGenerationContext getContext() {
        return this.context;
    }

    protected FieldScope getTestClassField(String fieldName) {
        ResolvedField resolvedField = Stream.of(this.testClassMembers.getMemberFields())
                .filter(field -> fieldName.equals(field.getName()))
                .findAny()
                .orElseGet(
                        () -> Stream.of(this.testClassMembers.getStaticFields())
                                .filter(field -> fieldName.equals(field.getName()))
                                .findAny()
                                .get());
        return this.context.getTypeContext().createFieldScope(resolvedField, this.testClassMembers);
    }

    protected MethodScope getTestClassMethod(String methodName) {
        ResolvedMethod resolvedMethod = Stream.of(this.testClassMembers.getMemberMethods())
                .filter(method -> methodName.equals(method.getName()))
                .findAny()
                .orElseGet(
                        () -> Stream.of(this.testClassMembers.getStaticMethods())
                                .filter(method -> methodName.equals(method.getName()))
                                .findAny()
                                .get());
        return this.context.getTypeContext().createMethodScope(resolvedMethod, this.testClassMembers);
    }
}
