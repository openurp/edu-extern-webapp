[#ftl]
[@b.head/]
<script type='text/javascript' src='${base}/dwr/engine.js'></script>
<script type='text/javascript' src='${base}/dwr/interface/otherExamSignUpService.js'></script>
[@b.toolbar title="校外考试及格率统计"]
  bar.addBack("${b.text("action.back")}");
[/@]
<table class="indexpanel">
<tr>
    <td class="index_view">
    [@b.form name="otherExamSignUpStatRatesearchForm" action="!passRate" title="ui.searchForm" target="otherExamSignUpStatRateList"]
     <table>
         <tr>
                 <td>学期:</td>
                 <td>
                    [@b.select  name="otherExamSignUp.semester.id" required="true" id="semestersId"  label="学年学期"]
                        <option>...</option>
                        [#list semesters?sort_by("code")?reverse as se]
                        <option value="${se.id}">${se.schoolYear}&nbsp;${se.name}</option>
                        [/#list]
                    [/@]
                </td>
                <td>考试类型:</td>
                <td>
                  [@b.select name="otherExamSignUp.subject.category.id" id="categoryId" label="考试类型" items=otherExamCategories onchange="kindIdSelect()" empty="..." /]
                </td>
                <td name="subjectId">科目名称:</td>
                <td>
                 [@b.select name="otherExamSignUp.subject.id" id="subjectId" label="科目名称" items=otherExternExamSubjects empty="..." /]
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
            [@b.div id="otherExamSignUpStatRateList" href="!passRate" /]
   </td>
</tr>
</table>
<script language="javascript">
  var form=document.otherExamSignUpStatRatesearchForm;
    function stat(){
       form.action="otherExamSignUpStat.action?method=passRate";
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
 </script>
[@b.foot/]