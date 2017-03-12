[#ftl]
[@b.head/]
[@b.toolbar title="校外考试成绩单"]
      bar.addPrint();
      bar.addBackOrClose();
[/@]
<DIV align="center" id="dayinDiv" name="dayinDiv">
  <table id="bar"></table>
</DIV> 
<h2 align="center">校外考试成绩单</h2> 
 [@b.grid items=otherGrades var="otherGrade"]
      [@b.row]
            [@b.col title="序号"]${otherGrade_index+1}[/@]
            [@b.col property="std.code" title="std.code"]
                <div style="padding-left:15px;text-align:left">
                    [@b.a href="studentSearch!info?student.id="+otherGrade.std.id title="${b.text('info.user.std')}" ]${(otherGrade.std.code)!}[/@]
                </div>
           [/@]
            [@b.col property="std.name" title="姓名"/]
            [@b.col property="semester.id" title="学期"]${(otherGrade.semester.schoolYear)!} &nbsp;${(otherGrade.semester.name)!}[/@]
            [@b.col property="subject.name" title="考试科目"/]
            [@b.col property="score" title="成绩"/]
            [#--][@b.col property="examNo" title="准考证号"/][--]
            [@b.col property="certificateNumber" title="证书编号"/]
        [/@]
[/@]
[@b.foot/]