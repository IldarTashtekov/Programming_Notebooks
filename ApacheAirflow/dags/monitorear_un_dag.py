
from airflow import DAG
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator
from airflow.utils.trigger_rule import TriggerRule
from datetime import datetime

def myfunction():
    raise Exception

with DAG(dag_id="monitoring_dag",
         description="probando monitorear",
         start_date=datetime(2022,11,12),
         end_date=datetime(2022,12,30),
         max_active_runs=2, #solo se ejecutaran 2 instancias al mismo tiempo
         schedule_interval="0 7 * * 1") as dag:

    t1 = BashOperator(task_id = "tarea1",
                      bash_command= "sleep 2 && echo 'Tarea 1'",
                      trigger_rule = TriggerRule.ALL_SUCCESS, #si todas han funcionado, entonces ejecuta la tarea
                      retries=2)
    
    t2 = BashOperator(task_id = "tarea2",
                      bash_command= "sleep 2 && echo 'Tarea 2'")

    
    t3 = PythonOperator(task_id = "tarea3",
                      python_callable = myfunction) #creamos una excepcion para monitorearla

    
    t4 = BashOperator(task_id = "tarea4",
                      bash_command= "sleep 2 && echo 'Tarea 4'",
                      trigger_rule = TriggerRule.ALWAYS) #se ejecuta aunque haya un error en la tarea anterior


    t1 >> t2 >> t3 >>t4



"""
////// T R I G G E R   R U L E S //////

ALL_SUCCESS -> si todas las tareas han sido exitosas, entonces ejecutate
ALL_FAILED -> si todas las tareas han fallado, entonces ejecutate

ALL_DONE -> si todas las tareas se han realizado, entonces ejecutate
ONE_SUCCESS -> si una tarea se ha realizado, entonces ejecutate

ONE_FAILED -> si una ha fallado
NONE_FAILED -> si ninguna ha fallado

ALWAYS -> siempre ejecutate
"""