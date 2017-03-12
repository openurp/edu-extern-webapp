[#ftl]
[@b.head/]
[@b.form name="otherExamFeeConfigForm" target="main" action="!save" theme="list" title="缴费设置维护"]
    [@b.select label="收费项目" required="true" items=feeTypes name="otherExamFeeConfig.feeType.id" value=(otherExamFeeConfig.feeType.id)! empty="..."/]
    [@b.select label="开放状态" items={'1':'开放','0':'关闭'} required="true" name="otherExamFeeConfig.opened" value=(otherExamFeeConfig.opened?string("1","0"))!/]
    [@b.startend label="开放时间范围" name="otherExamFeeConfig.openAt,otherExamFeeConfig.closeAt" start=(otherExamFeeConfig.openAt)! end=(otherExamFeeConfig.closeAt)! format="yyyy-MM-dd HH:mm" readonly="readonly"/]
    [@b.formfoot]
        <input type="hidden" value="${(otherExamFeeConfig.project.id)!(project.id)}" name="otherExamFeeConfig.project.id"/>
        <input type="hidden" value="${(otherExamFeeConfig.semester.id)!(semester.id)}" name="otherExamFeeConfig.semester.id"/>
        <input type="hidden" value="${(otherExamFeeConfig.id)!}" name="otherExamFeeConfig.id"/>
        [@b.submit value="action.submit"/]&nbsp;
        <input type="reset"  name="reset1" value="${b.text("action.reset")}" class="buttonStyle" />
    [/@]
[/@]
<script>
    jQuery(function(){
        document.otherExamFeeConfigForm["otherExamFeeConfig.openAt"].onfocus = function(){
            WdatePicker({
                dateFmt:'yyyy-MM-dd HH:mm',
                maxDate:'#F{$dp.$D(\''+ document.otherExamFeeConfigForm["otherExamFeeConfig.closeAt"].id+'\')}'
            });
        };
        document.otherExamFeeConfigForm["otherExamFeeConfig.closeAt"].onfocus = function(){
            WdatePicker({
                dateFmt:'yyyy-MM-dd HH:mm',
                minDate:'#F{$dp.$D(\''+ document.otherExamFeeConfigForm["otherExamFeeConfig.openAt"].id +'\')}'
            });        
        };
    });
</script>
[@b.foot/]
