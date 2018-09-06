package it.fbonfadelli.translation;

public interface TranslationRepository
{
  String retrieve(String key, String language);
}
