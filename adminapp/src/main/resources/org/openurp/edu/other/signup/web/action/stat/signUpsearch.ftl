[#ftl]
[@b.head/]
<script type='text/javascript' src='${base}/dwr/engine.js'></script>
<script type='text/javascript' src='${base}/dwr/interface/otherExamSignUpService.js'></script>
[@b.toolbar title="资格考试报名统计"]
    bar.addBack("${b.text("action.back")}");
[/@]
<table class="indexpanel">
<tr>
    <td class="index_view">
            <form name="otherExamSignUpStatsearchForm" action="!signUpInfo"  target="otherExamSignUpStatList" >
               <table>
                <tr>
                  <td>学期:</td>
                 <td>
                    [@b.select  name="signUp.semester.id" required="true" id="semestersId"  label="学年学期"]
                       <option>...</option>
                        [#list semesters?sort_by("code")?reverse as se]
                        <option value="${se.id}">${se.schoolYear}&nbsp;${se.name}</option>
                        [/#list]
                    [/@]
                </td>
                <td>考试类型:</td>
                <td>
                  [@b.select name="signUp.subject.category.id" id="categoryId" label="考试类型" items=otherExamCategories onchange="kindIdSelect()" empty="..." /]
                </td>
                <td name="subjectId">科目名称:</td>
                <td>
                 [@b.select name="signUp.subject.id" id="subjectId" label="科目名称" items=otherExamSubjects empty="..." /]
                </td>
                
                  <td colspan="2" align="center">
                     <button onclick="search()">查询</button>
                   </td>
                </tr>
                </table>
            </form>
    </td>
</tr>
<tr>
      <td class="index_content">
        [@b.div id="otherExamSignUpStatList" href="!signUpInfo" /]
    </td>
</tr>
</table>
<script>
        var otherExamSignUpStatsearchForm=document.otherExamSignUpStatsearchForm;
        function search(){
              bg.form.submit(otherExamSignUpStatsearchForm);
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