package com.example;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by Administrator on 2017/3/1 0001.
 *环境问题  今日代码没有注释
 * 注释写在Xmind上
 * 视频中有每一句的详解
 */
@AutoService(Processor.class)
public class BindViewProcessor  extends AbstractProcessor {
    /**
     */
    private Elements elementUtils;

    private Types typeUtils;
    /**
     */
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils=processingEnvironment.getElementUtils();
        typeUtils=processingEnvironment.getTypeUtils();
        filer=processingEnvironment.getFiler();
    }

    /**
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types=new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
//        types.add(Override.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeElement,List<FieldViewBinding>> targetMap=new HashMap<>();
        FileUtils.print("------------>    ");
        for(Element element:roundEnvironment.getElementsAnnotatedWith(BindView.class))
        {
            FileUtils.print("elment   "+element.getSimpleName().toString());
//            Log.i("")
            TypeElement enClosingElement= (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> list=targetMap.get(enClosingElement);
            if(list==null)
            {
                list=new ArrayList<>();
                targetMap.put(enClosingElement,list);//
            }
            String packageName=getPackageName(enClosingElement);
            int id=element.getAnnotation(BindView.class).value();
            String fieldName=element.getSimpleName().toString();
            TypeMirror typeMirror=element.asType();
            FileUtils.print("    typeMirror      "+typeMirror.getKind());
            FieldViewBinding fieldViewBinding=new FieldViewBinding(fieldName,typeMirror,id);
            list.add(fieldViewBinding);
        }
        for(Map.Entry<TypeElement,List<FieldViewBinding>> item:targetMap.entrySet())
        {
            List<FieldViewBinding> list=item.getValue();

            if(list==null||list.size()==0)
            {
                continue;
            }
            //enClosingElement  activity
            TypeElement enClosingElement=item.getKey();
            String packageName=getPackageName(enClosingElement);
            //MainActivity$$ViewBinder
            String complite=getClassName(enClosingElement,packageName);

            ClassName className=ClassName.bestGuess(complite);

            ClassName  viewBinder=ClassName.get("com.example.inject","ViewBinder");

            TypeSpec.Builder result=TypeSpec.classBuilder(complite+"$$ViewBinder")
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(TypeVariableName.get("T",className))
                    .addSuperinterface(ParameterizedTypeName.get(viewBinder,className));

            MethodSpec.Builder methodBuilder=MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addAnnotation(Override.class)
                    .addParameter(className,"target",Modifier.FINAL);
             for(int i=0;i<list.size();i++)
             {
                 FieldViewBinding fieldViewBinding=list.get(i);
                 //-->android.text.TextView
                 String pacckageNameString=fieldViewBinding.getType().toString();
                 ClassName viewClass=ClassName.bestGuess(pacckageNameString);
                 methodBuilder.addStatement
                         ("target.$L=($T)target.findViewById($L)",fieldViewBinding.getName()
                                 ,viewClass,fieldViewBinding.getResId());
             }

            result.addMethod(methodBuilder.build());

            try {
                JavaFile.builder(packageName,result.build())
                        .addFileComment("auto create make")
                        .build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return false;
    }
    //com.example.administrator.butterdepends.MainActivity
    private String getClassName(TypeElement enClosingElement, String packageName) {
        int packageLength=packageName.length()+1;
        return enClosingElement.getQualifiedName().toString().substring(packageLength).replace(".","$");
    }

    private String getPackageName(TypeElement enClosingElement) {
        //com.example.administrator.butterknifeframwork
      return   elementUtils.getPackageOf(enClosingElement).getQualifiedName().toString();
    }
}
