from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime

with DAG(dag_id="orquestracion",
         description="probando orquestracion",
         start_date=datetime(2022,11,12),
         end_date=datetime(2022,12,30),
         default_args={"depends_on_past":True}, #cada tarea depende de la tarea anterior
         schedule_interval="@daily") as dag:

    t1 = BashOperator(task_id = "tarea1",
                      bash_command= "sleep 2 && echo 'Tarea 1'")
    
    t2 = BashOperator(task_id = "tarea2",
                      bash_command= "sleep 2 && echo 'Tarea 2'")

    
    t3 = BashOperator(task_id = "tarea3",
                      bash_command= "sleep 2 && echo 'Tarea 3'")

    
    t4 = BashOperator(task_id = "tarea4",
                      bash_command= "sleep 2 && echo 'Tarea 4'")


    t1 >> t2 >> t3 >>t4