[#ftl]
[@b.head/]
<script type='text/javascript' src='${base}/dwr/engine.js'></script>
<script type='text/javascript' src='${base}/dwr/interface/otherExamSignUpService.js'></script>
[@b.toolbar title="校外考试成绩统计"]
    bar.addBack("${b.text("action.back")}");
[/@]
<table class="indexpanel">
<tr>
    <td class="index_view">
    [@b.form name="otherExamSignUpStatGradesearchForm" action="!statGrade" title="ui.searchForm" target="otherExamSignUpStatGradeList" ]
     <table>
                <tr>
                  <td>
                   [@b.textfield name="otherExamSignUp.std.grade" label="年级"/]
                  </td>
                <td>考试类型:</td>
                <td>
                  [@b.select name="otherExamSignUp.subject.category.id" id="categoryId" label="考试类型" items=otherExamCategories onchange="kindIdSelect()" empty="..." /]
                </td>
                <td name="subjectId">科目名称:</td>
                <td>
                 [@b.select name="otherExamSignUp.subject.id" id="subjectId" label="科目名称" items=otherExamSubjects empty="..." /]
                </td>
                <td align="center">
                     <button onclick="stat()">统计</button>
                   </td>
                    
                </tr>
        </table>
    [/@]
    </td>
</tr>
<tr>
    <td class="index_content">
        [@b.div id="otherExamSignUpStatGradeList" href="!statGrade" /]
    </td>
</tr>
</table>
<script language="javascript">
   var form=document.otherExamSignUpStatGradesearchForm;
    function stat(){
       form.action="otherExamSignUpStat.action?method=statGrade";
       bg.form.submit(form);
    }
   function kindIdSelect(){
           var categoryId=document.getElementById("categoryId").value;
           if(categoryId==null || categoryId==""){
               return;
           }
           //otherExamSignUpService.getConfigs(categoryId,setSelectConfig);
           otherExamSignUpService.getSubjects(categoryId,setSelectSubject);
   }
   function setSelectConfig(configs){
        subjectList=document.getElementById("configId");
        subjectList.options.length=0;
        subjectList.options.add(new Option("...",""));
        for(var prop in subjects){
            subjectList.options.add(new Option(getValueFrom(configs[prop],"name"),getValueFrom(configs[prop],"id")));
        }
   }
   function setSelectSubject(subjects){
           subjectList=document.getElementById("subjectId");
        subjectList.options.length=0;
        subjectList.options.add(new Option("...",""));
        for(var prop in subjects){
            subjectList.options.add(new Option(getValueFrom(subjects[prop],"name"),getValueFrom(subjects[prop],"id")));
        }
   }
   function getValueFrom(data, method) {
        return data[method];
   }
    function initShow(){
           document. form.submit();
   }
   initShow();
 </script>
[@b.foot/]