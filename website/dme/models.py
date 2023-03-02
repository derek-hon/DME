from django.db import models

# Create your models here.

class DMEConfig(models.Model):
    islands = models.IntegerField(default=0)
    date = models.DateField('Date Created')

    def __str__(self):
        output = "Islands: {}\n{}".format(self.islands, self.date)
        return output

class fitness(models.Model):
    name = models.CharField(max_length=200)
    description = models.TextField()

    def __str__(self):
        output = "Fitness Function: {}\nDescription: {}".format(self.name, self.description)
        return output

class DME_Map(models.Model):
    island_number = models.IntegerField(default=0)
    map_x = models.CharField(max_length=200)
    map_y = models.CharField(max_length=200)
    fitness = models.ForeignKey(fitness, on_delete=models.CASCADE, related_name='maps', default=None)
    config = models.ForeignKey(DMEConfig, on_delete=models.CASCADE, related_name='maps', default=None)
    image = models.ImageField(upload_to='images/%Y-%m-%d')

    def __str__(self):
        output = "X axis behaviour: {}\nY Axis Behaviour: {}\nFitness Function: ".format(self.map_x, self.map_y, self.fitness.__str__)
        return output