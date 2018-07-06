[#ftl]
[@b.head/]
[@b.toolbar title="报名统计"]
    var m=bar.addMenu("报名统计","signupInfo()");
    m.addItem("成绩统计","statGrade()");
    m.addItem("及格率统计","passRate()");

     function signupInfo(){
         var form = document.examSignupStatIndex;
         form.action = "${b.url('!signupInfo')}";
        bg.form.submit(form);
       }

      function statGrade(){
          var form = document.examSignupStatIndex;
         form.action = "${b.url('!statGrade')}";
          bg.form.submit(form);
       }

      function passRate(){
          var form = document.examSignupStatIndex;
         form.action = "${b.url('!passRate')}";
          bg.form.submit(form);
       }
[/@]
<table class="indexpanel">
    <tr>
        <td class="index_view">
            [@b.form theme="search" action="!signupInfo" name="examSignupStatIndex" title="ui.searchForm" target="examSignupStatList"]
                [@eams.semesterCalendar theme="search" label="学年学期" name="semester.id" empty=true value=semester/]
                [@b.select name="examSignup.subject.category.id" id="categoryId" label="考试类型" items=examCategories empty="..." /]
                [@b.select name="examSignup.subject.id" id="subjectId" label="科目名称" items={} empty="..." /]
            [/@]
        </td>
        <td class="index_content">
            [@b.div id="examSignupStatList" href="!signupInfo?semester.id=${semester.id}"/]
        </td>
    </tr>
</table>
<script>
    $(function(){
        var changeSubject = function(obj){
            var res = jQuery.post('${b.url("!categorySubject")}',{categoryId:obj.value},function(){
                $("#subjectId").empty();
                $("#subjectId").append("<option value=''>...</option>");
                if(res.status==200){
                    if(res.responseText!=""){
                        jQuery("#subjectId").append(res.responseText);
                    }
                }
            },"text");
        }

        $("#categoryId").change(function(){
            changeSubject(this);
        });

        $("#categoryId").change();
    })
</script>
[@b.foot/]
