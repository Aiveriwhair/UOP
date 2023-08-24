import json
import traceback
import datetime

outputPath = "logs/logs.log", "w+"


class Logger:
    @staticmethod
    def log(message, program):
        time = datetime.datetime.now()
        outputPath = f"logs/{program}.log"
        try:
            with open(outputPath, "a") as outputfile:
                outputfile.write(f"[{str(time)}]"+message + "\n")
        except FileNotFoundError:
            with open(outputPath, "w") as outputfile:
                outputfile.write(message + "\n")

    @staticmethod
    def debug(tag, message, program):
        if len(tag) > 20:
            tag = tag[:20]
        elif len(tag) < 20:
            tag = tag + " " * (20 - len(tag))
        Logger.log(f"[DEBUG]   {tag} | {message}", program)

    @staticmethod
    def info(tag, message, program):
        if len(tag) > 20:
            tag = tag[:20]
        elif len(tag) < 20:
            tag = tag + " " * (20 - len(tag))
        Logger.log(f"[INFO]    {tag} | {message}", program)

    @staticmethod
    def warning(tag, message, program):
        if len(tag) > 20:
            tag = tag[:20]
        elif len(tag) < 20:
            tag = tag + " " * (20 - len(tag))
        Logger.log(f"[WARNING] {tag} | {message}", program)

    @staticmethod
    def error(tag, message, program):
        if len(tag) > 20:
            tag = tag[:20]
        elif len(tag) < 20:
            tag = tag + " " * (20 - len(tag))
        Logger.log(f"[ERROR]   {tag} | {message}", program)

    @staticmethod
    def save(filename, tag, message, data):
        try:
            open("logs/"+ filename + ".log", "w+").write(f"[{tag}] | {message} \n{data.__str__()}")
            Logger.info("Logger save", "Saved " + filename + ".log")
        except:
            Logger.error("Logger save", "Failed to save " + filename + ".log", "LoggerSavingErrors")
            Logger.error("Logger save", traceback.format_exc(), "LoggerSavingErrors")
            


