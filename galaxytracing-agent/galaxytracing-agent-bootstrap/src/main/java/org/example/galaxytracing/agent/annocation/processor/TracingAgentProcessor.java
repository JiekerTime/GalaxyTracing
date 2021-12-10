/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.galaxytracing.agent.annocation.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.example.galaxytracing.agent.TracingAgent;
import org.example.galaxytracing.agent.annocation.Agent;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

/**
 * TracingAgent Processor.
 *
 * @author JiekerTime
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.example.galaxytracing.agent.annocation.Agent")
public final class TracingAgentProcessor extends AbstractProcessor {
    
    private static final String TRACING_AGENT_SUFFIX = "$$TracingAgent";
    
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            if (!roundEnv.processingOver()) {
                roundEnv.getElementsAnnotatedWith(Agent.class).forEach(element -> {
                    final FieldSpec agent = FieldSpec.builder(ClassName.get(TracingAgent.class), "tracingAgent")
                            
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                            .initializer("new $T()", ClassName.get(TracingAgent.class)).build();
                    final TypeSpec typeSpec = TypeSpec.classBuilder(element.getSimpleName() + TRACING_AGENT_SUFFIX)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addField(agent).build();
                    
                    final JavaFile javaFile = JavaFile.builder(processingEnv.getElementUtils().getPackageOf(element)
                            .getQualifiedName().toString(), typeSpec).build();
                    writeJavaFile(javaFile);
                });
            }
        }
        return true;
    }
    
    private void writeJavaFile(final JavaFile javaFile) {
        StringBuilder builder = new StringBuilder();
        
        JavaFileObject sourceFile = null;
        
        try {
            javaFile.writeTo(builder);
            
            String fileName = javaFile.packageName.isEmpty() ? javaFile.typeSpec.name : javaFile.packageName + "." + javaFile.typeSpec.name;
            List<Element> originatingElements = javaFile.typeSpec.originatingElements;
            sourceFile = processingEnv.getFiler().createSourceFile(fileName, originatingElements.toArray(new Element[0]));
            
            try (Writer writer = sourceFile.openWriter()) {
                writer.write(builder.toString());
            }
            
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Couldn't generate classes " + javaFile.packageName + '.' + javaFile.typeSpec.name);
            if (sourceFile != null) {
                sourceFile.delete();
            }
            
        }
    }
}
